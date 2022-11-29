import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { Dropdown, Layout, Space } from 'antd';
import { CaretDownOutlined } from '@ant-design/icons';

import './Navbar.scss';

import CustomBreadcrumb from './CustomBreadcrumb';
import { adminRoute, userRoute } from '../../routes/routes';

const Navbar = () => {
  const location = useLocation();
  const { Header } = Layout;
  const items = [
    {
      label: 'Change password',
      key: '0',
    },
    {
      label: 'Logout',
      key: '1',
    },
  ];
  const manageUser = [
    {
      title: 'Manage User',
      link: `/${adminRoute.home}/${adminRoute.manageUser}`,
    },
  ];
  const manageAsset = [
    {
      title: 'Manage Asset',
      link: `/${adminRoute.home}/${adminRoute.manageAsset}`,
    },
  ];
  const manageAssignment = [
    {
      title: 'Manage Assignment',
      link: `/${adminRoute.home}/${adminRoute.manageAssignment}`,
    },
  ];
  const manageRequestForReturning = [
    {
      title: 'Request for Returning',
      link: `/${adminRoute.home}/${adminRoute.requestForReturning}`,
    },
  ];
  const manageReport = [
    {
      title: 'Report',
      link: `/${adminRoute.home}/${adminRoute.report}`,
    },
  ];

  const [listTitles, setListTitles] = useState([{ title: '', link: '' }]);
  const user = useSelector((state) => state.user.user);

  useEffect(() => {
    const pathArray = location.pathname.split('/');
    const currentPath = Number(pathArray[pathArray.length - 1])
      ? pathArray[pathArray.length - 2]
      : pathArray[pathArray.length - 1];
    if (currentPath === '' || currentPath === adminRoute.home || currentPath === userRoute.home) {
      setListTitles([
        {
          title: 'Home',
          link: '#',
        },
      ]);
    } else if (currentPath === adminRoute.manageUser) {
      setListTitles([...manageUser]);
    } else if (currentPath === adminRoute.createUser) {
      setListTitles([
        ...manageUser,
        {
          title: 'Create New User',
          link: `#`,
        },
      ]);
    } else if (currentPath === adminRoute.editUser) {
      setListTitles([
        ...manageUser,
        {
          title: 'Edit User',
          link: `#`,
        },
      ]);
    } else if (currentPath === adminRoute.manageAsset) {
      setListTitles([...manageAsset]);
    } else if (currentPath === adminRoute.createAsset) {
      setListTitles([
        ...manageAsset,
        {
          title: 'Create New Asset',
          link: `#`,
        },
      ]);
    } else if (currentPath === adminRoute.editAsset) {
      setListTitles([
        ...manageAsset,
        {
          title: 'Edit Asset',
          link: `#`,
        },
      ]);
    } else if (currentPath === adminRoute.manageAssignment) {
      setListTitles([...manageAssignment]);
    } else if (currentPath === adminRoute.requestForReturning) {
      setListTitles([...manageRequestForReturning]);
    } else if (currentPath === adminRoute.report) {
      setListTitles([...manageReport]);
    } else {
      setListTitles([]);
    }
  }, [location]);

  return (
    <Header className="navbar-header">
      <div className="navbar-header__left">
        {listTitles &&
          listTitles.length > 0 &&
          listTitles.map((item, index) => {
            if (index === 0) {
              return (
                <Link key={`header-title-${index}`} to={item.link}>
                  <h3>{item.title}</h3>
                </Link>
              );
            }
            return <CustomBreadcrumb key={`header-title-${index}`} link={item.link} title={item.title} />;
          })}
      </div>
      <div className="navbar-header__right">
        <Dropdown menu={{ items }} trigger={['click']}>
          <a onClick={(e) => e.preventDefault()}>
            <Space>
              {user.username} <CaretDownOutlined />
            </Space>
          </a>
        </Dropdown>
      </div>
    </Header>
  );
};

export default Navbar;
