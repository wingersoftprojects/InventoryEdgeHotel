update user_detail set user_name=replace(user_name,'a','e') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'A','E') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'i','o') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'I','O') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'u','a') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'U','A') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'$','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'&','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,"'","") where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,"/","_") where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'_','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'.','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'-','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'7','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'1','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'4','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'(','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,')','') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=UCASE(user_name) where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=TRIM(user_name) where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'B','H') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'C','K') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'D','M') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'F','T') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'G','Y') where user_detail_id>0 and user_name not in('admin','system');
update user_detail set user_name=replace(user_name,'LTM','LTD') where user_detail_id>0 and user_name not in('admin','system');
