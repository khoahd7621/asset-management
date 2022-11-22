import './ManageUser.scss';

import { ListUser } from '../../../components';

const ManageUser = () => {
  return (
    <div className="manage-user">
      <h1 className="title-manage-user">User List</h1>
      <br></br>
      <ListUser />
    </div>
  );
};

export default ManageUser;
