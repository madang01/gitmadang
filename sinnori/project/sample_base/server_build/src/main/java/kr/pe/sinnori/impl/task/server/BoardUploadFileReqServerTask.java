package kr.pe.sinnori.impl.task.server;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardFileDTO.BoardFileDTO;
import kr.pe.sinnori.impl.message.BoardFileInfoDTO.BoardFileInfoDTO;
import kr.pe.sinnori.impl.message.BoardUploadFileReq.BoardUploadFileReq;
import kr.pe.sinnori.impl.message.BoardUploadFileReq.BoardUploadFileReq.NewAttachFile;
import kr.pe.sinnori.impl.message.BoardUploadFileReq.BoardUploadFileReq.SelectedOldAttachFile;
import kr.pe.sinnori.impl.message.BoardUploadFileRes.BoardUploadFileRes;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.impl.mybatis.MybatisSqlSessionFactoryManger;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardUploadFileReqServerTask extends AbstractServerTask {
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		// FIXME!
		log.info(inputMessage.toString());
		
		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		BoardUploadFileReq inObj = (BoardUploadFileReq)inputMessage;
		
		try {
			ValueChecker.checkValidUserId(inObj.getUserId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			MessageResultRes messageResultOutObj = new MessageResultRes();
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}		
		
		try {
			ValueChecker.checkValidIP(inObj.getIp());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			MessageResultRes messageResultOutObj = new MessageResultRes();
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		
		// long attachId = inObj.getAttachId();
		int selectedOldAttachFileCnt = inObj.getSelectedOldAttachFileCnt();
		int newAttachFileCnt = inObj.getNewAttachFileCnt();
		
		/*if (0 > selectedOldAttachFileCnt) {
			String errorMessage = new StringBuilder("업로드했던 파일들에 대한 사용자 선택 갯수[")
			.append(selectedOldAttachFileCnt).append("]가 0보다 작습니다.").toString();
			log.warn("{}, inObj=", errorMessage, inObj.toString());
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setResultMessage(errorMessage);
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		if (0 > newAttachFileCnt) {
			String errorMessage = new StringBuilder("신규 업로드 파일 갯수[")
			.append(newAttachFileCnt).append("]가 0보다 작습니다.").toString();
			log.warn("{}, inObj=", errorMessage, inObj.toString());
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setResultMessage(errorMessage);
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}*/
		
		long attachId = inObj.getAttachId();
		
		if (0 == attachId) {
			/** 업로드 신규 등록 */
			if (0 != selectedOldAttachFileCnt) {
				String errorMessage = "업로드 신규 등록:업로드했던 파일들에 대한 사용자 선택 목록이 존재합니다.";
				log.warn("{}, inObj=", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}
			
			if (0 == newAttachFileCnt) {
				String errorMessage = "업로드 신규 등록:업로드 파일이 존재하지 않습니다.";
				log.warn("{}, inObj=", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}
			
			if (newAttachFileCnt > ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT) {
				String errorMessage = new StringBuilder("업로드 신규 등록:신규 업로드 파일 갯수[")
				.append(newAttachFileCnt)
				.append("]가 최대 업로드 파일 갯수[")
				.append(ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT)
				.append("]를 초과 하였습니다.").toString();
				log.warn("{}, inObj={}", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}
			
			
			java.util.List<NewAttachFile> newAttachFileList = inObj.getNewAttachFileList();
			for (NewAttachFile newAttachFile : newAttachFileList) {
				String attachFileName = newAttachFile.getAttachFileName();
				
				if (attachFileName.equals("")) {
					String errorMessage = "업로드 신규 등록:업로드 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimAttachFileName = attachFileName.trim();
				if (!trimAttachFileName.equals(attachFileName)) {
					String errorMessage = "업로드 신규 등록:업로드 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String systemFileName = newAttachFile.getSystemFileName();		
				
				if (systemFileName.equals("")) {
					String errorMessage = "업로드 신규 등록:업로드 시스템 절대 경로 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimSystemFileName = systemFileName.trim();
				if (!trimSystemFileName.equals(systemFileName)) {
					String errorMessage = "업로드 신규 등록:업로드 시스템 절대 경로 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}				
			}
			
			BoardUploadFileRes outObj = null;
			
			SqlSession session = sqlSessionFactory.openSession(false);
			try {			
				int cntOfInsertBoardFileInfo= session.insert("insertBoardFileInfo", inObj);
				
				if (0 == cntOfInsertBoardFileInfo) {
					session.rollback();
					
					String errorMessage = "업로드 신규 등록:게시판 파일 정보 등록 처리가 실패하였습니다.";
					log.warn("{}, inObj={}", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				Long retAttachId = session.selectOne("getAttachId");
				
				if (null == retAttachId) {				
					session.rollback();
					
					String errorMessage = "업로드 신규 등록:업로드 식별자 조회가 실패하였습니다.";
					log.warn("{}, inObj={}", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				} else {					
					// java.util.List<BoardFileDTO>  attachFileDTOList = new java.util.ArrayList<BoardFileDTO>();
					
					short attachSeq = 0;
					for (NewAttachFile newAttachFile : newAttachFileList) {						
						BoardFileDTO newBoardFileDTO = new BoardFileDTO();
						newBoardFileDTO.setAttachId(retAttachId);
						newBoardFileDTO.setAttachSeq(attachSeq++);
						newBoardFileDTO.setAttachFileName(newAttachFile.getAttachFileName());
						newBoardFileDTO.setSystemFileName(newAttachFile.getSystemFileName());
						
						int cntOfInsertBoardFile = session.insert("insertBoardFile", newBoardFileDTO);
						
						if (cntOfInsertBoardFile == 0) {
							session.rollback();
							
							String errorMessage = "업로드 신규 등록:개별 업로드 파일 추가 처리가 실패하였습니다.";
							log.warn("{}, 실패한 개별 업로드 파일={}, inObj={}", errorMessage, newBoardFileDTO.toString(), inObj.toString());
							
							MessageResultRes messageResultOutObj = new MessageResultRes();
							messageResultOutObj.setIsSuccess(false);
							messageResultOutObj.setTaskMessageID(inObj.getMessageID());
							messageResultOutObj.setResultMessage(errorMessage);
							toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
							return;
						}
						
						log.debug("attachFileDTOList add newAttachFile={}", newBoardFileDTO.toString());
					}
					
					// session.commit();
					
					outObj = session.selectOne("getBoardUploadFileOutDTO", retAttachId);
					if (null == outObj) {
						session.rollback();
						
						String errorMessage = "업로드 신규 등록:업로드 신규 등록 처리 결과 조회가 실패하였습니다.";
						log.warn("{}, inObj={}", errorMessage, inObj.toString());
						
						MessageResultRes messageResultOutObj = new MessageResultRes();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(inObj.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
						return;
					}
					
					outObj.setAttachFileCnt(outObj.getAttachFileList().size());
					
					// FIXME!
					log.debug("outObj={}", outObj.toString());
					
					session.commit();
					
					toLetterCarrier.addSyncOutputMessage(outObj);
					return;
				}			
			} catch(Exception e) {
				session.rollback();
				
				String errorMessage = new StringBuilder("업로드 신규 등록:알수 없는 이유로 업로드 신규 등록 처리가 실패하였습니다. inObj=")
				.append(inObj.toString().toString()).toString();				
				log.warn(errorMessage, e);
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			} finally {
				session.close();
			}
			
		} else {
			/** 업로드 수정 */
			
			int totalAttachFile = newAttachFileCnt + selectedOldAttachFileCnt;
			
			/*if (0 == totalAttachFile) {
				String errorMessage = new StringBuilder("업로드 수정:업로드 파일이 존재하지 않습니다.").toString();
				log.warn("{}, inObj={}", errorMessage, inObj.toString());
				
				MessageResult messageResultOutObj = new MessageResult();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}*/	
			
			if (ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT < totalAttachFile) {
				String errorMessage = new StringBuilder("업로드 수정:기존 업로드 파일등중 사용자가 선택한 갯수[")
				.append(selectedOldAttachFileCnt)
				.append("]와 신규 추가한 업로드 파일 개수[")
				.append(newAttachFileCnt)
				.append("]의 합이 최대 업로드 파일 개수[")
				.append(ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT)
				.append("]를 초과 하였습니다.").toString();
				log.warn("{}, inObj={}", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}			
					
			java.util.List<NewAttachFile> newAttachFileList = inObj.getNewAttachFileList();
			
			for (NewAttachFile newAttachFile : newAttachFileList) {
				String attachFileName = newAttachFile.getAttachFileName();
				
				if (attachFileName.equals("")) {
					String errorMessage = "업로드 수정:업로드 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimAttachFileName = attachFileName.trim();
				if (!trimAttachFileName.equals(attachFileName)) {
					String errorMessage = "업로드 수정:업로드 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String systemFileName = newAttachFile.getSystemFileName();		
				
				if (systemFileName.equals("")) {
					String errorMessage = "업로드 수정:업로드 시스템 절대 경로 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimSystemFileName = systemFileName.trim();
				if (!trimSystemFileName.equals(systemFileName)) {
					String errorMessage = "업로드 수정:업로드 시스템 절대 경로 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}				
			}		
			
			java.util.List<SelectedOldAttachFile> selectedOldAttachFileList = inObj.getSelectedOldAttachFileList();			
			java.util.List<BoardFileDTO> attachFileDTOList = new ArrayList<BoardFileDTO>();			
			BoardUploadFileRes outObj = null;			
			
			SqlSession session = sqlSessionFactory.openSession(false);
			try {
				BoardFileInfoDTO boardFileInfoDTO = session.selectOne("getBoardFileInfoDTOInLock", inObj);
				if (null == boardFileInfoDTO) {
					session.rollback();
					
					String errorMessage = "업로드 수정:업로드 파일 정보가 존재하지 않습니다.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				// FIXME!
				log.info("업로드 수정:변경전 업로드 파일 정보={}, inObj={}", boardFileInfoDTO.toString(), inObj.toString());
				
				/** 소유자 검사 */
				if (! boardFileInfoDTO.getOwnerId().equals(inObj.getUserId())) {
					session.rollback();
					
					String errorMessage = new StringBuilder("업로드 수정:업로드 수정 요청자[")
					.append(inObj.getUserId())
					.append("] 와 업로드 파일 소유자[")
					.append(boardFileInfoDTO.getOwnerId())
					.append("] 가 다릅니다.").toString();
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				
				
				int cntOfUpdateUploadFileInfo = session.update("updateUploadFileInfo", inObj);
				if (0 == cntOfUpdateUploadFileInfo) {
					session.rollback();
					
					String errorMessage = "업로드 수정:업로드 파일 정보 수정 처리가 실패하였습니다.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				
				for (SelectedOldAttachFile selectedOldAttachFile : selectedOldAttachFileList) {
					BoardFileDTO userSelectedBoardFileDTO = new BoardFileDTO();
					userSelectedBoardFileDTO.setAttachId(attachId);
					userSelectedBoardFileDTO.setAttachSeq(selectedOldAttachFile.getAttachSeq());
					
					BoardFileDTO  oldBoardFileDTO = session.selectOne("getBoardFileDTO", userSelectedBoardFileDTO);
					if (null == oldBoardFileDTO) {
						session.rollback();
						
						String errorMessage = "업로드 수정:사용자가 선택한 업로드 파일이 존재하지 않습니다.";
						log.warn("{}, 사용자가 선택한 업로드 파일={}, inObj=", errorMessage, selectedOldAttachFile.toString(), inObj.toString());
						
						MessageResultRes messageResultOutObj = new MessageResultRes();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(inObj.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
						return;
					}
					
					oldBoardFileDTO.setAttachSeq((short)attachFileDTOList.size());
					
					
					log.debug("attachFileDTOList add oldAttachFile={}", oldBoardFileDTO.toString());
					
					attachFileDTOList.add(oldBoardFileDTO);					
				}
				
				
				for (NewAttachFile newAttachFile : newAttachFileList) {
					BoardFileDTO newBoardFileDTO = new BoardFileDTO();
					newBoardFileDTO.setAttachId(attachId);
					newBoardFileDTO.setAttachSeq((short)attachFileDTOList.size());
					newBoardFileDTO.setAttachFileName(newAttachFile.getAttachFileName());
					newBoardFileDTO.setSystemFileName(newAttachFile.getSystemFileName());
					
					log.debug("attachFileDTOList add newAttachFile={}", newBoardFileDTO.toString());
					
					attachFileDTOList.add(newBoardFileDTO);	
				}
							
				
				// FIXME!
				// log.info("attachFileDTOList size={}", attachFileDTOList.size());
				
				session.delete("deleteAllUploadFiles", inObj);
				
				for (BoardFileDTO boardFileDTO : attachFileDTOList) {
					int cntOfInsertBoardFile = session.insert("insertBoardFile", boardFileDTO);
					if (0 == cntOfInsertBoardFile) {
						session.rollback();
						
						String errorMessage = "업로드 수정:개별 업로드 파일 추가 처리가 실패하였습니다.";
						log.warn("{}, 개별 업로드 파일={}, inObj=", errorMessage, boardFileDTO.toString(), inObj.toString());
						
						MessageResultRes messageResultOutObj = new MessageResultRes();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(inObj.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
						return;
					}
					
					// FIXME!
					log.info("insertBoardFile, boardFileDTO={}", boardFileDTO.toString());
				}
				
				
				
				outObj = session.selectOne("getBoardUploadFileOutDTO", inObj.getAttachId());
				if (null == outObj) {
					session.rollback();
					
					String errorMessage = "업로드 수정:업로드 수정 처리 결과 조회가 실패하였습니다.";
					log.warn("{}, inObj={}", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				outObj.setAttachFileCnt(outObj.getAttachFileList().size());
				
				// FIXME!
				log.debug("outObj={}", outObj.toString());
				
				session.commit();
				
				toLetterCarrier.addSyncOutputMessage(outObj);
				return;
			} catch(Exception e) {
				session.rollback();
				
				String errorMessage = new StringBuilder("업로드 수정:알수 없는 이유로 업로드 수정 처리가 실패하였습니다. inObj=")
				.append(inObj.toString().toString()).toString();				
				log.warn(errorMessage, e);
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			} finally {
				session.close();
			}
		}
	}
}
