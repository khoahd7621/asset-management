import { Outlet } from 'react-router-dom';
import { Layout } from 'antd';

import './Layout.scss';

import { Navbar, Sidebar } from '../components';

const User = () => {
  return (
    <Layout className="main-layout">
      <Navbar />
      <Layout className="sub-layout">
        <Sidebar />
        <Outlet />
      </Layout>
    </Layout>
  );
};

export default User;
