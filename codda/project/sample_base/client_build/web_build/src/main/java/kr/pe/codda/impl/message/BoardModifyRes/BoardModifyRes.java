/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.impl.message.BoardModifyRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardModifyRes message
 * @author Won Jonghoon
 *
 */
public class BoardModifyRes extends AbstractMessage {
	private short boardID;
	private long boardNo;
	private int deletedAttachedFileCnt;

	public static class DeletedAttachedFile {
		private short attachedFileSeq;

		public short getAttachedFileSeq() {
			return attachedFileSeq;
		}

		public void setAttachedFileSeq(short attachedFileSeq) {
			this.attachedFileSeq = attachedFileSeq;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DeletedAttachedFile[");
			builder.append("attachedFileSeq=");
			builder.append(attachedFileSeq);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<DeletedAttachedFile> deletedAttachedFileList;

	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public long getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(long boardNo) {
		this.boardNo = boardNo;
	}
	public int getDeletedAttachedFileCnt() {
		return deletedAttachedFileCnt;
	}

	public void setDeletedAttachedFileCnt(int deletedAttachedFileCnt) {
		this.deletedAttachedFileCnt = deletedAttachedFileCnt;
	}
	public java.util.List<DeletedAttachedFile> getDeletedAttachedFileList() {
		return deletedAttachedFileList;
	}

	public void setDeletedAttachedFileList(java.util.List<DeletedAttachedFile> deletedAttachedFileList) {
		this.deletedAttachedFileList = deletedAttachedFileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardModifyRes[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", deletedAttachedFileCnt=");
		builder.append(deletedAttachedFileCnt);

		builder.append(", deletedAttachedFileList=");
		if (null == deletedAttachedFileList) {
			builder.append("null");
		} else {
			int deletedAttachedFileListSize = deletedAttachedFileList.size();
			if (0 == deletedAttachedFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < deletedAttachedFileListSize; i++) {
					DeletedAttachedFile deletedAttachedFile = deletedAttachedFileList.get(i);
					if (0 == i) {
						builder.append("deletedAttachedFile[");
					} else {
						builder.append(", deletedAttachedFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(deletedAttachedFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}