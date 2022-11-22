import { Layout } from 'antd';

import '../components/FormLogin/FormLogin.scss';
import NavbarLogin from '../components/NavbarLogin/NavbarLogin';
import FormLogin from '../components/FormLogin/FormLogin';

const Login = () => {
  return (
    <Layout className="main-layout">
      <NavbarLogin />
      <div className="form-login-wrapper">
        <h3 className="form-login-title">Welcome to Online Asset Management</h3>
        <hr />
        <div className="form-login">
          <FormLogin />
        </div>
      </div>
    </Layout>
  );
};

export default Login;
