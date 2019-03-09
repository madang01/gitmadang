select * from SB_SEQ_TB;

select * from sb_member_tb;

select * from sb_board_tb where board_id = 1;

select * from sb_board_history_tb where board_id = 1;

select * from sb_board_filelist_tb where board_id = 1;

select * from sb_board_info_tb where board_id = 1;

select JSON_OBJECT("menu_no", menu_no, "parent_no", parent_no, "depth", depth, "order_sq", order_sq, "menu_nm", menu_nm, "link_url", link_url)  from sb_sitemenu_tb order by order_sq asc;

select count(*) from sb_board_tb where board_id = 1;


select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq
from sb_board_tb as a 
inner join (select a.board_id, a.board_no from sb_board_tb as a force INDEX (primary) where a.board_id = 1 and a.parent_no = 0 and a.board_st = 'Y' order by board_no desc limit 0, 20) as b
on a.board_id = b.board_id and a.board_no = b.board_no;


select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq
	from sb_board_tb as a 
	inner join (select a.board_id, a.group_no, a.group_sq from sb_board_tb as a force INDEX (sb_board_idx1) where a.board_id = 1 and a.board_st = 'Y' order by group_no desc, group_sq desc limit 0, 20) as b
	on a.board_id = b.board_id and a.group_no = b.group_no and a.group_sq = b.group_sq;


select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq,
b.subject, b.registrant_id as last_modifier_id, b.ip as last_modifier_ip, b.reg_dt as last_mod_dt,
c.registrant_id as first_writer_id, c.ip as first_writer_ip, c.reg_dt as first_reg_dt
from (select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq
	from sb_board_tb as a 
	inner join (select a.board_id, a.group_no, a.group_sq from sb_board_tb as a force INDEX (sb_board_idx1) where a.board_id = 1 and a.board_st = 'Y' order by group_no desc, group_sq desc limit 0, 20) as b
	on a.board_id = b.board_id and a.group_no = b.group_no and a.group_sq = b.group_sq
	) as a
inner join sb_board_history_tb as b
on a.board_id = b.board_id and a.board_no = b.board_no
and b.history_sq = (select max(sb_board_history_tb.history_sq) 
	from sb_board_history_tb where a.board_id = sb_board_history_tb.board_id
    and a.board_no = sb_board_history_tb.board_no)
inner join sb_board_history_tb as c
on a.board_id = c.board_id and a.board_no = c.board_no
and c.history_sq = 0
order by a.group_no desc, a.group_sq desc;