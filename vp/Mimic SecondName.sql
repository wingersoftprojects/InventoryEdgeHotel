update user_detail set second_name=replace(second_name,'a','e') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'A','E') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'i','o') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'I','O') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'u','a') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'U','A') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'$','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'&','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,"'","") where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,"/","_") where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'_','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'.','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'-','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'7','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'1','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'4','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'(','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,')','') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=UCASE(second_name) where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=TRIM(second_name) where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'B','H') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'C','K') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'D','M') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'F','T') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'G','Y') where user_detail_id>0 and second_name not in('admin','system');
update user_detail set second_name=replace(second_name,'LTM','LTD') where user_detail_id>0 and second_name not in('admin','system');
