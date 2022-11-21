import { Dropdown, Layout, Space } from 'antd';
import CustomBreadcrumb from './CustomBreadcrumb';
import { CaretDownOutlined } from '@ant-design/icons';

import './Navbar.scss';

const Navbar = () => {
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

  return (
    <Header className="navbar-header">
      <div className="navbar-header__left">
        <h3>Home</h3>
        {/* <CustomBreadcrumb title="Demo" /> */}
      </div>
      <div className="navbar-header__right">
        <Dropdown
          menu={{
            items,
          }}
          trigger={['click']}
        >
          <a onClick={(e) => e.preventDefault()}>
            <Space>
              username
              <CaretDownOutlined />
            </Space>
          </a>
        </Dropdown>
      </div>
    </Header>
  );
};

export default Navbar;
