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

package kr.pe.sinnori.common.io.dhb.header;

/**
 * 파일 헤더 클래스.
 * 
 * <pre>
 * 파일을 교환을 하기 위해서 만든 특수 메시지가 파일 데이터(FileData)와 파일 정보(FileInfo) 메시지이다.
 * 파일 정보 메시지는 파일명, 크기 그리고 파일 식별자이다. 
 * 파일 데이터 메시지는 파일 데이터 메시지 헤더와 파일 데이터 바디 부분으로 구성된다.
 * </pre>
 * 
 * @author Jonghoon Won
 * 
 */
public class DHBFileHeader extends DHBMessageHeader {
	
	public DHBFileHeader(int messageIDFixedSize) {
		super(messageIDFixedSize);
	}

	/** fileID : long 8byte */
	public long fileID;

	/** filePieceNo : unsinged int 4byte */
	public long filePieceNo;

	/** totalFilePieces : unsinged int 4byte */
	public long totalFilePieces;

	/** filePieceDataSize : int 4byte */
	public int filePieceSize;

	// public byte[] filePiece;

	public static final int FILE_HEADER_BYTESIZE = 20;
	// public static final int FILE_BODY_MAX_BYTESIZE = MESSAGE_BODY_MAX_BYTESIZE	- FILE_HEADER_BYTESIZE;

	@Override
	public String toString() {
		StringBuffer headerInfo = new StringBuffer();
		headerInfo.append(super.toString());

		headerInfo.append(", file id=[");
		headerInfo.append(fileID);
		headerInfo.append("]");

		headerInfo.append(", file piece No=[");
		headerInfo.append(filePieceNo);
		headerInfo.append("]");

		headerInfo.append(", total file pieces=[");
		headerInfo.append(totalFilePieces);
		headerInfo.append("]");

		headerInfo.append(", file piece size=[");
		headerInfo.append(filePieceSize);
		headerInfo.append("]");
		/*
		 * if (null != filePiece) { headerInfo.append(", file piece in Body=[");
		 * headerInfo.append(HexUtil.byteArrayAllToHex(filePiece));
		 * headerInfo.append("]"); }
		 */

		return headerInfo.toString();
	}

	/*
	 * public static final int fileID_offset =
	 * MESSAGE_HEADER_BYTESIZE+DHB_DATA_HEADER_BYTESIZE;; public static final
	 * int fileID_bytesize = 8;
	 * 
	 * public static final int filePieceNo_offset = fileID_offset +
	 * fileID_bytesize; public static final int filePieceNo_bytesize = 4;
	 * 
	 * public static final int totalFilePieces_offset = filePieceNo_offset +
	 * filePieceNo_bytesize; public static final int totalFilePieces_bytesize =
	 * 4;
	 * 
	 * public static final int filePieceSize_offset = totalFilePieces_offset +
	 * totalFilePieces_bytesize; public static final int filePieceSize_bytesize
	 * = 4;
	 * 
	 * public static final int filePiece_offset = filePieceSize_offset +
	 * filePieceSize_bytesize; public static final int filePiece_bytesize =
	 * (Integer) conf .getResource("io_buffer_size") - filePiece_offset;
	 */
}
