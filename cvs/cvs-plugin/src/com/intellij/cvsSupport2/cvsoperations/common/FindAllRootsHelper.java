/*
 * Copyright 2000-2024 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.cvsSupport2.cvsoperations.common;

import com.intellij.cvsSupport2.CvsUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.vcsUtil.VcsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.intellij.util.containers.ContainerUtil.map;

public class FindAllRootsHelper {
  private FindAllRootsHelper() { }

  public static List<VirtualFile> findVersionedUnder(final List<? extends VirtualFile> coll) {
    final List<FilePath> pathList = map(coll, VcsUtil::getFilePath);
    final MyVisitor visitor = new MyVisitor();

    for (FilePath root : pathList) {
      final VirtualFile vf = root.getVirtualFile();
      if (vf == null) continue;
      VfsUtilCore.visitChildrenRecursively(vf, visitor);
    }

    return visitor.found;
  }

  @NotNull
  public static <S> List<S> filterUniqueRoots(@NotNull List<S> in, @NotNull Function<? super S, ? extends VirtualFile> convertor) {
    // 检查输入列表是否为空
    if (in.isEmpty()) return in;
    
    // 创建结果列表
    List<S> result = new ArrayList<>();
    Map<String, VirtualFile> paths = new HashMap<>();
    
    // 遍历输入列表
    for (S s : in) {
      VirtualFile file = convertor.apply(s);
      if (file == null) continue;
      
      String path = file.getPath();
      boolean shouldAdd = true;
      
      // 检查是否是其他文件的父目录或子目录
      for (Map.Entry<String, VirtualFile> entry : paths.entrySet()) {
        String existingPath = entry.getKey();
        VirtualFile existingFile = entry.getValue();
        
        if (VfsUtilCore.isAncestor(existingFile, file, false)) {
          // 已有文件是当前文件的父目录，不添加当前文件
          shouldAdd = false;
          break;
        } else if (VfsUtilCore.isAncestor(file, existingFile, false)) {
          // 当前文件是已有文件的父目录，移除已有文件
          paths.remove(existingPath);
          // 在结果列表中移除对应的元素
          for (int i = 0; i < result.size(); i++) {
            if (convertor.apply(result.get(i)) == existingFile) {
              result.remove(i);
              break;
            }
          }
        }
      }
      
      if (shouldAdd) {
        paths.put(path, file);
        result.add(s);
      }
    }
    
    return result;
  }

  private static class MyVisitor extends VirtualFileVisitor<Void> {
    private final List<VirtualFile> found = new LinkedList<>();

    @NotNull
    @Override
    public Result visitFileEx(@NotNull VirtualFile file) {
      if (CvsUtil.fileIsUnderCvsMaybeWithVfs(file)) {
        found.add(file);
      }
      return file.isDirectory() && found.contains(file) ? SKIP_CHILDREN : CONTINUE;
    }
  }
}
