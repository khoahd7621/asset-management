import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';

import './Sidebar.scss';

import logoNash from '../../assets/logo.png';
import { adminRoute, userRoute } from '../../routes/routes';

const Sizebar = () => {
  const location = useLocation();
  const adminItems = [
    {
      key: 'Home',
      label: 'Home',
      path: `/${adminRoute.home}`,
    },
    {
      key: 'Manage User',
      label: 'Manage User',
      path: `/${adminRoute.home}/${adminRoute.manageUser}`,
    },
    {
      key: 'Manage Asset',
      label: 'Manage Asset',
      path: `/${adminRoute.home}/${adminRoute.manageAsset}`,
    },
    {
      key: 'Manage Assignment',
      label: 'Manage Assignment',
      path: `/${adminRoute.home}/${adminRoute.manageAssignment}`,
    },
    {
      key: 'Request for Returning',
      label: 'Request for Returning',
      path: `/${adminRoute.home}/${adminRoute.requestForReturning}`,
    },
    {
      key: 'Report',
      label: 'Report',
      path: `/${adminRoute.home}/${adminRoute.report}`,
    },
  ];

  const staffItems = [
    {
      key: 'Home',
      label: 'Home',
      path: userRoute.home,
    },
  ];

  const [currentIndex, setCurrentIndex] = useState(0);
  const user = useSelector((state) => state.user.user);

  useEffect(() => {
    const currentPath = location.pathname.slice(location.pathname.lastIndexOf('/') + 1, location.pathname.length);
    if (currentPath === '' || currentPath === adminRoute.home || currentPath === userRoute.home) {
      setCurrentIndex(adminItems.findIndex((item) => item.key === 'Home'));
    } else if (currentPath === adminRoute.manageUser || currentPath === adminRoute.createUser) {
      setCurrentIndex(adminItems.findIndex((item) => item.key === 'Manage User'));
    } else if (currentPath === adminRoute.manageAsset) {
      setCurrentIndex(adminItems.findIndex((item) => item.key === 'Manage Asset'));
    } else if (currentPath === adminRoute.manageAssignment) {
      setCurrentIndex(adminItems.findIndex((item) => item.key === 'Manage Assignment'));
    } else if (currentPath === adminRoute.requestForReturning) {
      setCurrentIndex(adminItems.findIndex((item) => item.key === 'Request for Returning'));
    } else if (currentPath === adminRoute.report) {
      setCurrentIndex(adminItems.findIndex((item) => item.key === 'Report'));
    } else {
      setCurrentIndex(adminItems.findIndex((item) => item.key === 'Home'));
    }
  }, [location]);

  return (
    <div className="main-side-bar">
      <Link to={user.role === 'ADMIN' ? `/${adminRoute.home}` : `/${userRoute.home}`}>
        <img className="logo" src={logoNash} alt="Asset Application" title="Asset Application" />
        <div className="title">Online Asset Management</div>
      </Link>
      <div className="list-items">
        {user.role === 'ADMIN' &&
          adminItems.map((item, index) => (
            <div key={item.key} className={`item ${index === currentIndex ? 'active' : ''}`}>
              <Link to={item.path}>{item.label}</Link>
            </div>
          ))}
        {user.role === 'STAFF' &&
          staffItems.map((item, index) => (
            <div key={item.key} className={`item ${index === currentIndex ? 'active' : ''}`}>
              <Link to={item.path}>{item.label}</Link>
            </div>
          ))}
      </div>
    </div>
  );
};

export default Sizebar;
