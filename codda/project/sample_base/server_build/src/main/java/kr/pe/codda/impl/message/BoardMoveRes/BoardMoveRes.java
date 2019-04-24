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

package kr.pe.codda.impl.message.BoardMoveRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardMoveRes message
 * @author Won Jonghoon
 *
 */
public class BoardMoveRes extends AbstractMessage {
	private short sourceBoardID;
	private long sourceBoardNo;
	private short targetBoardID;
	private int cnt;

	public static class BoardMoveInfo {
		private long fromBoardNo;
		private long toBoardNo;
		private int attachedFileCnt;

		public static class AttachedFile {
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
				builder.append("AttachedFile[");
				builder.append("attachedFileSeq=");
				builder.append(attachedFileSeq);
				builder.append("]");
				return builder.toString();
			}
		}

		private java.util.List<AttachedFile> attachedFileList;

		public long getFromBoardNo() {
			return fromBoardNo;
		}

		public void setFromBoardNo(long fromBoardNo) {
			this.fromBoardNo = fromBoardNo;
		}
		public long getToBoardNo() {
			return toBoardNo;
		}

		public void setToBoardNo(long toBoardNo) {
			this.toBoardNo = toBoardNo;
		}
		public int getAttachedFileCnt() {
			return attachedFileCnt;
		}

		public void setAttachedFileCnt(int attachedFileCnt) {
			this.attachedFileCnt = attachedFileCnt;
		}
		public java.util.List<AttachedFile> getAttachedFileList() {
			return attachedFileList;
		}

		public void setAttachedFileList(java.util.List<AttachedFile> attachedFileList) {
			this.attachedFileList = attachedFileList;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("BoardMoveInfo[");
			builder.append("fromBoardNo=");
			builder.append(fromBoardNo);
			builder.append(", toBoardNo=");
			builder.append(toBoardNo);
			builder.append(", attachedFileCnt=");
			builder.append(attachedFileCnt);

			builder.append(", attachedFileList=");
			if (null == attachedFileList) {
				builder.append("null");
			} else {
				int attachedFileListSize = attachedFileList.size();
				if (0 == attachedFileListSize) {
					builder.append("empty");
				} else {
					builder.append("[");
					for (int i=0; i < attachedFileListSize; i++) {
						AttachedFile attachedFile = attachedFileList.get(i);
						if (0 == i) {
							builder.append("attachedFile[");
						} else {
							builder.append(", attachedFile[");
						}
						builder.append(i);
						builder.append("]=");
						builder.append(attachedFile.toString());
					}
					builder.append("]");
				}
			}
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<BoardMoveInfo> boardMoveInfoList;

	public short getSourceBoardID() {
		return sourceBoardID;
	}

	public void setSourceBoardID(short sourceBoardID) {
		this.sourceBoardID = sourceBoardID;
	}
	public long getSourceBoardNo() {
		return sourceBoardNo;
	}

	public void setSourceBoardNo(long sourceBoardNo) {
		this.sourceBoardNo = sourceBoardNo;
	}
	public short getTargetBoardID() {
		return targetBoardID;
	}

	public void setTargetBoardID(short targetBoardID) {
		this.targetBoardID = targetBoardID;
	}
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public java.util.List<BoardMoveInfo> getBoardMoveInfoList() {
		return boardMoveInfoList;
	}

	public void setBoardMoveInfoList(java.util.List<BoardMoveInfo> boardMoveInfoList) {
		this.boardMoveInfoList = boardMoveInfoList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardMoveRes[");
		builder.append("sourceBoardID=");
		builder.append(sourceBoardID);
		builder.append(", sourceBoardNo=");
		builder.append(sourceBoardNo);
		builder.append(", targetBoardID=");
		builder.append(targetBoardID);
		builder.append(", cnt=");
		builder.append(cnt);

		builder.append(", boardMoveInfoList=");
		if (null == boardMoveInfoList) {
			builder.append("null");
		} else {
			int boardMoveInfoListSize = boardMoveInfoList.size();
			if (0 == boardMoveInfoListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < boardMoveInfoListSize; i++) {
					BoardMoveInfo boardMoveInfo = boardMoveInfoList.get(i);
					if (0 == i) {
						builder.append("boardMoveInfo[");
					} else {
						builder.append(", boardMoveInfo[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(boardMoveInfo.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}