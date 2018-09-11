delete from SINNORIDB.TW_BOARD_INFO_TB where board_id >= 1 and board_id <= 10;

ALTER TABLE `SINNORIDB`.`TW_BOARD_INFO_TB` 
AUTO_INCREMENT = 1 ;

INSERT INTO `SINNORIDB`.`TW_BOARD_INFO_TB`
(`board_id`,
`board_name`,
`board_gubun`,
`board_info`)
VALUES
(0, '공지 게시판',
0,
'공지를 목적으로 하는 관리자 전용 게시판');

INSERT INTO `SINNORIDB`.`TW_BOARD_INFO_TB`
(`board_id`,
`board_name`,
`board_gubun`,
`board_info`)
VALUES
(0, '자유 게시판',
0,
'일반 회원 게시판');


INSERT INTO `SINNORIDB`.`TW_SEQ_MANAGER_TB`
(`sq_type_id`,
`sq_value`)
VALUES
(1,
1);


SELECT b.*
FROM (
	SELECT board_id, group_no, group_sq
FROM SB_BOARD_TB 
FORCE INDEX (sb_board_idx1)
where board_id = 1 and board_st = 'Y'
and group_no >= 0 and group_sq >= 0
ORDER BY GROUP_NO desc, GROUP_SQ desc
LIMIT 0, 20) a 
inner join SB_BOARD_TB b 
	on a.board_id = b.board_id
	and a.group_no = b.group_no
	and a.group_sq = b.group_sq
order by b.group_no desc, b.group_sq desc
;




SELECT b.*
FROM (
	SELECT board_id, board_no
FROM SB_BOARD_TB 
FORCE INDEX (primary)
WHERE BOARD_ID = 1 
and board_no >= 0
AND board_no=group_no
and BOARD_ST = 'Y'
ORDER BY board_no desc
LIMIT 0, 20) a 
inner join SB_BOARD_TB b 
on a.board_id = b.board_id
and a.board_no = b.board_no
order by b.board_no desc
;