-- MySQL Workbench Synchronization
-- Generated: 2019-03-30 20:27
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: madang02

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `SB_DB` DEFAULT CHARACTER SET utf8 ;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_INFO_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '게시판 식별자,\n1 : 공지, 2:자유, 3:이슈',
  `board_name` VARCHAR(30) NULL DEFAULT NULL COMMENT '게시판 이름',
  `list_type` TINYINT(4) NOT NULL COMMENT '게시판 목록 유형, 0:그룹 루트, 1; 계층',
  `reply_policy_type` TINYINT(4) NOT NULL COMMENT '댓글 정책 유형, 0:댓글없음, 1:본문글에만, 2:본문및 댓글 모두',
  `write_permission_type` TINYINT(4) NOT NULL COMMENT '본문 쓰기 권한 유형, 0:어드민, 1:일반인, 2:손님',
  `reply_permission_type` TINYINT(4) NOT NULL COMMENT '댓글 쓰기 권한 유형, 0:어드민, 1:일반인, 2:손님, 주) \'댓글 쓰기 권한 유형\'은 \'상세 유형\'에서 \'0 댓글없음\' 이 아닌 경우만 유효하다',
  `cnt` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '게시판 목록 개수',
  `total` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '게시판 글 전체 개수',
  `next_board_no` INT(10) UNSIGNED NOT NULL DEFAULT 1 COMMENT '다음 게시판 번호, 본문의 부모 게시판 번호가 0 으로 예약되어 있어 1부터 시작',
  PRIMARY KEY (`board_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_MEMBER_TB` (
  `user_id` VARCHAR(20) NOT NULL COMMENT '사용자 아이디',
  `nickname` VARCHAR(45) NOT NULL COMMENT '별명',
  `pwd_base64` VARCHAR(88) NOT NULL COMMENT '비밀번호, 비밀번호는 해쉬 값으로 변환되어 base64 형태로 저장된다.',
  `pwd_salt_base64` VARCHAR(12) NOT NULL COMMENT '비밀번호를 해쉬로 바꿀때 역 추적 방해를 목적으로 함께 사용하는 랜덤 값',
  `role` TINYINT(4) NOT NULL COMMENT '회원 역할, A:관리자, M:일반회원, SELECT char(ascii(\'A\') using ascii);',
  `state` TINYINT(4) NOT NULL COMMENT '회원 상태, Y : 정상, B:블락, W:탈퇴, SELECT char(ascii(\'Y\') using ascii);',
  `pwd_hint` TINYTEXT NULL DEFAULT NULL COMMENT '비밀번호 힌트, 비밀번호 분실시 답변 유도용 사용자한테 보여주는 힌트',
  `pwd_answer` TINYTEXT NULL DEFAULT NULL COMMENT '비밀번호 답변, 비밀번호 분실시 맞춘다면 비밀번호 재 설정 혹은 비밀번호 초기화를 진행한다.',
  `pwd_fail_cnt` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '비밀번호 틀린 횟수, 로그인시 비밀번호 틀릴 경우 1 씩 증가하며 최대 n 번까지 시도 가능하다.  비밀번호를 맞쳤을 경우 0 으로 초기화 된다.',
  `reg_dt` DATETIME NULL DEFAULT NULL COMMENT '회원 가입일',
  `last_mod_dt` DATETIME NULL DEFAULT NULL COMMENT '마지막 회원 정보 수정일',
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `sb_member_idx1` (`nickname` ASC),
  INDEX `sb_member_idx2` (`state` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '게시판 종류 식별자, 어떤 게시판인지 설명하는 게시판 정보(board_info) 테이블을 바라본다.',
  `board_no` INT(10) UNSIGNED NOT NULL COMMENT '게시판 번호,  1부터 시작한다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 게시판 타입별로 게시판 번호를 얻어옴',
  `group_no` INT(10) UNSIGNED NOT NULL COMMENT '그룹 번호',
  `group_sq` SMALLINT(5) UNSIGNED NOT NULL COMMENT '그룹 즉 동일한 그룹 번호(=group_no)  에서 0 부터 시작되는 순번',
  `parent_no` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT '부모 게시판 번호,  게시판 번호는 1부터 시작하며 부모가 없는 경우 부모 게시판 번호는 0 값을 갖는다.',
  `depth` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT '트리 깊이,  0 부터 시작하며 트리 깊이가 0 일 경우 최상위 글로써 최상위 글을 기준으로 이후 댓글이 달린다. 자식 글의 댓글 깊이는 부모 글의 댓글 깊이보다 1 이 크다.',
  `view_cnt` INT(11) NULL DEFAULT NULL COMMENT '조회수',
  `board_st` TINYINT(4) NOT NULL COMMENT '게시글 상태, B : 블락, D : 삭제된 게시글, Y : 정상 게시글,  SELECT char(ascii(\'Y\') using ascii);',
  `next_attached_file_sq` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT '다음 첨부 파일 시퀀스, 처음 0부터 시작',
  `pwd_base64` VARCHAR(88) NULL DEFAULT NULL COMMENT '게시글 비밀번호, 손님의 경우 반듯이 게시글 비밀번호를 입력한다. 게시글 수정할때 이 값이 null  이 아니면 게시글 비밀번호를 입력한것으로 간주하여 값 일치한경우에만 수정을 허용한다',
  PRIMARY KEY (`board_id`, `board_no`),
  INDEX `sb_board_fk1_idx` (`board_id` ASC),
  INDEX `sb_board_idx1` (`board_id` DESC, `board_st` DESC, `group_no` DESC, `group_sq` DESC),
  INDEX `sb_board_idx2` (`board_id` DESC, `board_st` DESC, `parent_no` DESC, `board_no` DESC),
  CONSTRAINT `sb_board_fk1`
    FOREIGN KEY (`board_id`)
    REFERENCES `SB_DB`.`SB_BOARD_INFO_TB` (`board_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_FILELIST_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `attached_file_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '첨부 파일 순번',
  `attached_fname` VARCHAR(255) NULL DEFAULT NULL COMMENT '첨부 파일 이름',
  `attached_fsize` BIGINT(20) NULL DEFAULT NULL COMMENT '첨부 파일 크기',
  PRIMARY KEY (`board_id`, `board_no`, `attached_file_sq`),
  CONSTRAINT `sb_board_filelist_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_VOTE_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `user_id` VARCHAR(20) NOT NULL,
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  `reg_dt` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`board_no`, `user_id`, `board_id`),
  CONSTRAINT `sb_board_vote_fk2`
    FOREIGN KEY (`user_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sb_board_vote_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SEQ_TB` (
  `sq_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '시퀀스 식별자, 0:메뉴, 1:공지게시판 시퀀스, 2:자유게시판시퀀스, 3:FAQ시퀀스',
  `sq_value` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT '시퀀스 값, 1 부터 시작',
  `sq_name` VARCHAR(45) NULL DEFAULT NULL COMMENT '시퀀스 이름',
  PRIMARY KEY (`sq_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SITEMENU_TB` (
  `menu_no` INT(10) UNSIGNED NOT NULL COMMENT '메뉴 번호,  1부터 시작된다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 메뉴 번호를 얻어옴.',
  `parent_no` INT(10) UNSIGNED NOT NULL COMMENT '부모 메뉴 번호,  메뉴 번호는 1부터 시작되며 부모가 없는 경우 부모 메뉴 번호 값은  0 값을 갖는다.',
  `depth` TINYINT(3) UNSIGNED NOT NULL COMMENT '트리 깊이,  0 부터 시작하며 부모보다 + 1 이 크다',
  `order_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '전체 메뉴 순서',
  `menu_nm` VARCHAR(100) NOT NULL COMMENT '메뉴 이름',
  `link_url` VARCHAR(2048) NOT NULL COMMENT '메뉴에 대응되는 링크 주소',
  PRIMARY KEY (`menu_no`),
  INDEX `sb_sitemenu_idx1` (`order_sq` ASC),
  INDEX `sb_sitemenu_idx2` (`parent_no` ASC, `order_sq` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_HISTORY_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `history_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '히스토리 순서',
  `subject` VARCHAR(255) NULL DEFAULT NULL,
  `contents` MEDIUMTEXT NULL DEFAULT NULL,
  `registrant_id` VARCHAR(20) NOT NULL COMMENT '작성자',
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  `reg_dt` DATETIME NULL DEFAULT NULL COMMENT '최초 작성일',
  PRIMARY KEY (`board_id`, `board_no`, `history_sq`),
  INDEX `sb_board_history_fk2_idx` (`registrant_id` ASC),
  CONSTRAINT `sb_board_history_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sb_board_history_fk2`
    FOREIGN KEY (`registrant_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SITE_LOG_TB` (
  `yyyymmdd` CHAR(8) NOT NULL COMMENT '로그 날짜, yyyyMMdd 형태의 8자리 년월일 문자',
  `day_log_sq` INT(10) UNSIGNED NOT NULL COMMENT '일일 로그 순번',
  `user_id` VARCHAR(20) NULL DEFAULT NULL COMMENT '사용자 아이디',
  `log_txt` TEXT NULL DEFAULT NULL COMMENT '로그 내용, 로그로 남기는 내용 (1) 회원 가입, (2) 회원 탈퇴, (3) 사용자 차단, (4) 사용자 차단 해제, (5) 게시글 차단, (6) 게시글 차단 해제,  (7) 관리자 로그인',
  `reg_dt` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`yyyymmdd`, `day_log_sq`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_MEMBER_ACTIVITY_HISTORY_TB` (
  `user_id` VARCHAR(20) NOT NULL COMMENT '사용자 아이디, 활동을 한 회원 아이디',
  `activity_sq` BIGINT(20) NOT NULL COMMENT '활동 순번, 0 부터 시작되며 이후 MAX + 1 이 된다',
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '게시판 식별자,  활동  대상이 되는 게시글의 게시판 식별자',
  `board_no` INT(10) UNSIGNED NOT NULL COMMENT '게시판 번호, 활동 대상이 되는 게시글의 게시판 번호',
  `activity_type` TINYINT(4) NOT NULL COMMENT '사용자 활동 종류, \'W\'(=87):게시글 작성, \'R\'(=82):게시글 댓글, \'V\'(=86):게시글 추천, \'D\'(=68):게시글 삭제, SELECT char(ascii(\'W\') using ascii);',
  `reg_dt` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `activity_sq`),
  INDEX `board_id_idx` (`board_id` ASC, `board_no` ASC),
  CONSTRAINT `user_activity_history_fk1`
    FOREIGN KEY (`user_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_activity_hisotry_fk2`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
