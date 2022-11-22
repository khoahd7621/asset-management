import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Dropdown, Layout, Space } from 'antd';
import CustomBreadcrumb from './CustomBreadcrumb';
import { CaretDownOutlined } from '@ant-design/icons';

import './Navbar.scss';
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
  const [listTitles, setListTitles] = useState(['Home']);

  useEffect(() => {
    const currentPath = location.pathname.slice(location.pathname.lastIndexOf('/') + 1, location.pathname.length);
    if (currentPath === '' || currentPath === adminRoute.home || currentPath === userRoute.home) {
      setListTitles(['Home']);
    } else if (currentPath === adminRoute.manageUser) {
      setListTitles(['Manage User']);
    } else if (currentPath === adminRoute.createUser) {
      setListTitles(['Manage User', 'Create New User']);
    } else if (currentPath === adminRoute.manageAsset) {
      setListTitles(['Manage Asset']);
    } else if (currentPath === adminRoute.manageAssignment) {
      setListTitles(['Manage Assignment']);
    } else if (currentPath === adminRoute.requestForReturning) {
      setListTitles(['Manage Request for Returning']);
    } else if (currentPath === adminRoute.report) {
      setListTitles(['Manage Report']);
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
              return <h3 key={`header-title-${index}`}>{item}</h3>;
            }
            return <CustomBreadcrumb key={`header-title-${index}`} title={item} />;
          })}
      </div>
      <div className="navbar-header__right">
        <Dropdown menu={{ items }} trigger={['click']}>
          <a onClick={(e) => e.preventDefault()}>
            <Space>
              username <CaretDownOutlined />
            </Space>
          </a>
        </Dropdown>
      </div>
    </Header>
  );
};

export default Navbar;
