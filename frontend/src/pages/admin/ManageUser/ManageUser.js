import { useEffect } from 'react';

import './ManageUser.scss';

import { ListUser } from '../../../components';

const ManageUser = () => {
  useEffect(() => {
    document.title = 'Manage User - User List';
  }, []);

  return (
    <div className="manage-user">
      <h1 className="title-manage-user">User List</h1>
      
      <ListUser />
    </div>
  );
};

export default ManageUser;
