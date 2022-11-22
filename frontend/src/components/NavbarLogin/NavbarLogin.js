import { Layout } from 'antd';

import '../Navbar/Navbar.scss';
import logoNash from '../../assets/logo.png';

const NavbarLogin = () => {
  const { Header } = Layout;

  return (
    <Header className="navbar-header">
      <div className="navbar-header__left login">
        <img
          className="logo"
          src={logoNash}
          alt="Asset Application"
          title="Asset Application"
          style={{ alignSelf: 'center' }}
        />
        <h3 className="titleLogin">Online Asset Management</h3>
      </div>
    </Header>
  );
};

export default NavbarLogin;
