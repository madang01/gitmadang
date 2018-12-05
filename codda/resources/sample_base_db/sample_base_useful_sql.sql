select max(group_sq) from sb_board_tb
where board_id = 1
and parent_no = 1
and group_sq < 2;


select a.board_id, a.board_no, 
a.group_no, a.group_sq, a.parent_no, a.depth, 
a.board_st,
b.subject, b.modifier_id, b.ip, b.reg_dt 
from sb_board_tb a, sb_board_history_tb b
where a.board_id=b.board_id and a.board_no=b.board_no
order by a.group_no desc, a.group_sq desc;

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


