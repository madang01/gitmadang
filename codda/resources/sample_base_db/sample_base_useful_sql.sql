select * from SB_SEQ_TB;

select * from sb_member_tb;

select * from sb_board_tb where board_id = 1;

select * from sb_board_history_tb where board_id = 1;

select * from sb_board_filelist_tb where board_id = 1;

select * from sb_board_info_tb where board_id = 1;

select JSON_OBJECT("menu_no", menu_no, "parent_no", parent_no, "depth", depth, "order_sq", order_sq, "menu_nm", menu_nm, "link_url", link_url)  from sb_sitemenu_tb order by order_sq asc;

select count(*) from sb_board_tb where board_id = 1;

-- for group root list query
select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq
from sb_board_tb as a 
inner join (select a.board_id, a.parent_no, a.board_no from sb_board_tb as a force INDEX (sb_board_idx2) where a.board_id = 1 and a.parent_no = 0 and a.board_st = 'Y' order by board_no desc limit 0, 20) as b
on a.board_id = b.board_id 
and a.parent_no = b.parent_no
and a.board_no = b.board_no;

-- for group root list query
select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq,
b.subject, b.registrant_id as last_modifier_id, b.ip as last_modifier_ip, b.reg_dt as last_mod_dt,
c.registrant_id as first_writer_id, c.ip as first_writer_ip, c.reg_dt as first_reg_dt
from (select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq
from sb_board_tb as a 
inner join (select a.board_id, a.parent_no, a.board_no from sb_board_tb as a force INDEX (sb_board_idx2) where a.board_id = 1 and a.parent_no = 0 and a.board_st = 'Y' order by board_no desc limit 0, 20) as b
on a.board_id = b.board_id and a.parent_no = b.parent_no and a.board_no = b.board_no) as a
inner join sb_board_history_tb as b
on a.board_id = b.board_id and a.board_no = b.board_no
and b.history_sq = (select max(sb_board_history_tb.history_sq) 
	from sb_board_history_tb where a.board_id = sb_board_history_tb.board_id
    and a.board_no = sb_board_history_tb.board_no)
inner join sb_board_history_tb as c 
on c.board_id = a.board_id  and c.board_no = a.board_no 
and c.history_sq = 0
order by a.group_no desc, a.group_sq desc;


-- for tree list query
select a.board_id, a.board_no, a.group_no, a.group_sq, a.parent_no, a.depth, a.view_cnt, a.board_st, a.next_attached_file_sq
	from sb_board_tb as a 
	inner join (select a.board_id, a.group_no, a.group_sq from sb_board_tb as a force INDEX (sb_board_idx1) where a.board_id = 1 and a.board_st = 'Y' order by group_no desc, group_sq desc limit 0, 20) as b
	on a.board_id = b.board_id and a.group_no = b.group_no and a.group_sq = b.group_sq;

-- for tree list query
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


-- 게시판 index
ALTER TABLE `lt_sb_db`.`SB_BOARD_TB` 
DROP INDEX `sb_board_idx1` ,
ADD UNIQUE INDEX `sb_board_idx1` (`board_id` ASC, `group_no` ASC, `group_sq` ASC, `board_st` ASC),
DROP INDEX `sb_board_idx2` ,
ADD UNIQUE INDEX `sb_board_idx2` (`board_id` ASC, `parent_no` ASC, `board_no` ASC, `board_st` ASC);

-- 개인 활동 내역
select activity_sq, board_id, board_no, char(activity_type using ascii) as activity_type 
from SB_USER_ACTIVITY_HISTORY_TB
where user_id = 'guest' 
order by activity_sq desc;


-- 개인 활동 이력 조회
select
	a.activity_sq, a.board_id,  a.board_no, 	
	a.activity_type, a.board_st, a.group_no,
    a.REG_DT, a.user_id, a.LIST_TYPE,	
	b.SUBJECT as groupSubject, c.registrant_id as groupWriterID,
    d.SUBJECT as ownSubject, d.registrant_id as ownWriterID
from (
	select SB_MEMBER_ACTIVITY_HISTORY_TB.activity_sq, 
		char(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE using ascii) as activity_type, 
		SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT,
		SB_MEMBER_ACTIVITY_HISTORY_TB.user_id,
		SB_BOARD_INFO_TB.LIST_TYPE,
        SB_BOARD_TB.board_id,
        SB_BOARD_TB.board_no,
        SB_BOARD_TB.board_st,
        SB_BOARD_TB.group_no
	from  SB_MEMBER_ACTIVITY_HISTORY_TB
	inner join SB_BOARD_INFO_TB
		on SB_BOARD_INFO_TB.board_id = SB_MEMBER_ACTIVITY_HISTORY_TB.board_id
	inner join SB_BOARD_TB
		on SB_BOARD_TB.board_id = SB_MEMBER_ACTIVITY_HISTORY_TB.board_id
		and SB_BOARD_TB.board_no = SB_MEMBER_ACTIVITY_HISTORY_TB.board_no
	where SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID = 'test01'
	and SB_MEMBER_ACTIVITY_HISTORY_TB.activity_sq >= 0
	and SB_MEMBER_ACTIVITY_HISTORY_TB.activity_sq < 20
) as a
inner join SB_BOARD_HISTORY_TB b
	on b.board_id = a.board_id
	and b.board_no = a.group_no
	and b.history_sq = 
	(
	select max(HISTORY_SQ) 
	from SB_BOARD_HISTORY_TB
	where SB_BOARD_HISTORY_TB.board_id = a.board_id
	and SB_BOARD_HISTORY_TB.board_no = a.group_no
	)    
inner join SB_BOARD_HISTORY_TB c
	on c.board_id = a.board_id
	and c.board_no = a.group_no
	and c.history_sq = 0
inner join SB_BOARD_HISTORY_TB d
	on d.board_id = a.board_id
	and d.board_no = a.board_no
	and d.history_sq = 
	(
	select max(HISTORY_SQ) 
	from SB_BOARD_HISTORY_TB
	where SB_BOARD_HISTORY_TB.board_id = a.board_id
	and SB_BOARD_HISTORY_TB.board_no = a.board_no
	)
order by a.activity_sq desc;


select
	a.activity_sq, a.board_id,  a.board_no, 	
	a.activity_type, a.board_st, a.group_no,
	a.list_type,
    a.REG_DT, a.user_id, a.LIST_TYPE,	
	b.SUBJECT as groupSubject, c.registrant_id as groupWriterID,
    d.SUBJECT as ownSubject, d.registrant_id as ownWriterID
from (
	select SB_MEMBER_ACTIVITY_HISTORY_TB.activity_sq, 
		char(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE using ascii) as activity_type, 
		SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT,
		SB_MEMBER_ACTIVITY_HISTORY_TB.user_id,
		(select LIST_TYPE from SB_BOARD_INFO_TB where SB_BOARD_INFO_TB.board_id = SB_MEMBER_ACTIVITY_HISTORY_TB.board_id) as list_type,
        SB_BOARD_TB.board_id,
        SB_BOARD_TB.board_no,
        SB_BOARD_TB.board_st,
        SB_BOARD_TB.group_no
	from  SB_MEMBER_ACTIVITY_HISTORY_TB	
	inner join SB_BOARD_TB
		on SB_BOARD_TB.board_id = SB_MEMBER_ACTIVITY_HISTORY_TB.board_id
		and SB_BOARD_TB.board_no = SB_MEMBER_ACTIVITY_HISTORY_TB.board_no
	where SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID = 'test01'
	and SB_MEMBER_ACTIVITY_HISTORY_TB.activity_sq >= 0
	and SB_MEMBER_ACTIVITY_HISTORY_TB.activity_sq < 20
) as a
inner join SB_BOARD_HISTORY_TB b
	on b.board_id = a.board_id
	and b.board_no = a.group_no
	and b.history_sq = 
	(
	select max(HISTORY_SQ) 
	from SB_BOARD_HISTORY_TB
	where SB_BOARD_HISTORY_TB.board_id = a.board_id
	and SB_BOARD_HISTORY_TB.board_no = a.group_no
	)    
inner join SB_BOARD_HISTORY_TB c
	on c.board_id = a.board_id
	and c.board_no = a.group_no
	and c.history_sq = 0
inner join SB_BOARD_HISTORY_TB d
	on d.board_id = a.board_id
	and d.board_no = a.board_no
	and d.history_sq = 
	(
	select max(HISTORY_SQ) 
	from SB_BOARD_HISTORY_TB
	where SB_BOARD_HISTORY_TB.board_id = a.board_id
	and SB_BOARD_HISTORY_TB.board_no = a.board_no
	)
order by a.activity_sq desc;
