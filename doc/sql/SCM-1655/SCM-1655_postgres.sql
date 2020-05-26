create table wip_item_wkp_pos(
	id varchar(36) ,
	item_id varchar(36) not null,
	location_a varchar(50) default null,
	location_b varchar(50) default null,
	unit_qty int4 default null,
	location_a_qty int4 default null,
	technique_attr varchar(50) default null,
	rmk01 varchar(200) default null,
	rmk02 varchar(200) default null,
	rmk03 varchar(200) default null,
	rmk04 varchar(200) default null,
	rmk05 varchar(200) default null,
	crt_user varchar(36),
	crt_host varchar(30),
	crt_time timestamp,
	upd_user varchar(36),
	upd_host varchar(30),
	upd_time timestamp,
	constraint pk_wip_item_wkp_pos primary key (id)
);
comment on table wip_item_wkp_pos is '整机螺丝类物料工艺属性基表';
comment on column wip_item_wkp_pos.id is '主键';
comment on column wip_item_wkp_pos.item_id is '物料id';
comment on column wip_item_wkp_pos.location_a is '位置a';
comment on column wip_item_wkp_pos.location_b is '位置b';
comment on column wip_item_wkp_pos.unit_qty is '物料单位用量';
comment on column wip_item_wkp_pos.location_a_qty is '位置a部件数量';
comment on column wip_item_wkp_pos.technique_attr is '工艺属性';
comment on column wip_item_wkp_pos.rmk01 is '';
comment on column wip_item_wkp_pos.rmk02 is '';
comment on column wip_item_wkp_pos.rmk03 is '';
comment on column wip_item_wkp_pos.rmk04 is '';
comment on column wip_item_wkp_pos.rmk05 is '';
