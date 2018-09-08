select * from SB_SEQ_TB;

select * from sb_board_tb where board_id = 1;

select * from sb_board_history_tb where board_id = 1;

select * from sb_board_filelist_tb where board_id = 1;

select * from sb_board_info_tb where board_id = 1;


select count(*) from sb_board_tb where board_id = 1;


select sb_board_tb.*, last_board_history_tb.subject, last_board_history_tb.content, last_board_history_tb.ip, last_board_history_tb.reg_dt mod_dt 
from sb_board_tb
join sb_board_history_tb last_board_history_tb
on sb_board_tb.board_id = last_board_history_tb.board_id and sb_board_tb.board_no = last_board_history_tb.board_no
where sb_board_tb.board_id = 1 
	and sb_board_tb.board_st = 'y'
	and last_board_history_tb.history_sq = (select max(sb_board_history_tb.history_sq) 
from sb_board_history_tb where sb_board_tb.board_id = sb_board_history_tb.board_id
    and sb_board_tb.board_no = sb_board_history_tb.board_no)
order by group_no desc, group_sq asc
limit 0, 20;


