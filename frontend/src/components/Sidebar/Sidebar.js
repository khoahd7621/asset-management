import { Link } from 'react-router-dom';

import './Sidebar.scss';

import logoNash from '../../assets/logo.png';
import { adminRoute } from '../../routes/routes';

const Sizebar = () => {
  const adminItems = [
    {
      key: 'Home',
      label: 'Home',
      path: adminRoute.home,
    },
    {
      key: 'Manage User',
      label: 'Manage User',
      path: adminRoute.home + adminRoute.manageUser,
    },
    {
      key: 'Manage Asset',
      label: 'Manage Asset',
      path: adminRoute.home + adminRoute.manageAsset,
    },
    {
      key: 'Manage Assignment',
      label: 'Manage Assignment',
      path: adminRoute.home + adminRoute.manageAssignment,
    },
    {
      key: 'Request for Returning',
      label: 'Request for Returning',
      path: adminRoute.home + adminRoute.requestForReturning,
    },
    {
      key: 'Report',
      label: 'Report',
      path: adminRoute.home + adminRoute.report,
    },
  ];

  const userItems = [
    {
      key: 'Home',
      label: 'Home',
      path: adminRoute.home,
    },
  ];

  return (
    <div className="main-side-bar">
      <img className="logo" src={logoNash} alt="Asset Application" title="Asset Application" />
      <div className="title">Online Asset Management</div>
      <div className="list-items">
        {adminItems.map((item) => (
          <div key={item.key} className={`item ${false ? 'active' : ''}`}>
            <Link to={item.path}>{item.label}</Link>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Sizebar;
