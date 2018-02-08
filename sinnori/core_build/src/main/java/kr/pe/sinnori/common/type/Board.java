package kr.pe.sinnori.common.type;

public abstract class Board {
	// 시퀀스 종류 식별자, 0:업로드 파일 이름 시퀀스
	public enum SeqType {
		UPLOAD_FILE_NAME(0);
		
		private int seqTypeID;
		private SeqType(int seqTypeID) {
			this.seqTypeID = seqTypeID;
		}
		
		public int getSeqTypeID() {
			return seqTypeID;
		}
		
		public static SeqType valueOf(int seqTypeID) {
			SeqType[] seqTypes = SeqType.values();
			for (SeqType seqType : seqTypes) {
				if (seqType.getSeqTypeID() == seqTypeID) {
					return seqType;
				}
			}
			throw new IllegalArgumentException("the parameter seqTypeID[" + seqTypeID + "] is a unknown seq type id");
		}
	}

	// 회원 구분, 0:관리자, 1:일반회원
	public enum MemberType {
		ADMIN(0), USER(1);
		
		private int memberTypeID;
		private MemberType(int memberTypeID) {
			this.memberTypeID = memberTypeID;
		}
		
		public int getMemberTypeID() {
			return memberTypeID;
		}

		public static MemberType valueOf(int memberTypeID) {
			MemberType[] memberTypes = MemberType.values();
			for (MemberType memberType : memberTypes) {
				if (memberType.getMemberTypeID() == memberTypeID) {
					return memberType;
				}
			}			
			throw new IllegalArgumentException("the parameter memberTypeID[" + memberTypeID + "] is a unknown member type id");			
		}
	}
	
	/** 회원 상태 ,  0:정상, 1:블락, 2:탈퇴 */
	public enum MemberStateType {
		OK(0), BLOCK(1), MEMBER_LEAVE(2);
		
		private int memeberStateTypeID;
		
		private MemberStateType(int memeberStateTypeID) {
			this.memeberStateTypeID = memeberStateTypeID;
		}
		public int getMemeberStateTypeID() {
			return memeberStateTypeID;
		}
		
		public static MemberStateType valueOf(int memeberStateTypeID) {
			MemberStateType[] memeberStateTypes = MemberStateType.values();
			for (MemberStateType memeberStateType : memeberStateTypes) {
				if (memeberStateType.getMemeberStateTypeID() == memeberStateTypeID) {
					return memeberStateType;
				}
			}	
			
			throw new IllegalArgumentException("the parameter memeberStateTypeID["+memeberStateTypeID+"] is a unknown member state type id\"");
			
		}
	}
}
