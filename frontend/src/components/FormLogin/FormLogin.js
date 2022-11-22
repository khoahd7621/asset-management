import { Button, Form, Input } from 'antd';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import Validation from '../../utils/validation';
import { toast } from 'react-toastify';
import { postLogin } from '../../services/authApiService';
import { fetchUserLoginSuccess } from '../../redux/slice/userSlice';
import { adminRoute, userRoute } from '../../routes/routes';
import jwt_decode from 'jwt-decode';

const FormLogin = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [user, setUser] = useState({
    username: '',
    password: '',
  });
  const [isSending, setIsSending] = useState(false);

  useEffect(() => {
    document.title = 'Login';
  }, []);

  const handleChangeInput = (event) => {
    setUser({
      ...user,
      [event.target.name]: event.target.value,
    });
  };

  const handleClearInput = () => {
    setUser({
      username: '',
      password: '',
    });
  };

  const handleSubmitLogin = async () => {
    if (!Validation.isMinLength(user.password, 6)) {
      toast.error('Password must be at least 6 characters');
      return;
    }
    if (!Validation.isMaxLength(user.password, 24)) {
      toast.error('Password must be at most 24 characters');
      return;
    }
    setIsSending(true);
    const response = await postLogin(user.username, user.password);
    if (response && response.status === 200) {
      handleClearInput();
      const token = response.data.accessToken;
      const decodeToken = jwt_decode(token);
      dispatch(
        fetchUserLoginSuccess({
          username: decodeToken.username,
          location: decodeToken.location,
          isFirstLogin: response.data.isFirstLogin,
          role: decodeToken.role,
          accessToken: token,
        }),
      );
      if (decodeToken.role === 'ADMIN') {
        navigate(adminRoute.home);
      } else {
        navigate(userRoute.home);
      }
      toast.success('Login success!');
    } else {
      toast.error('Username or password is incorrect. Please try again!');
    }
    setIsSending(false);
  };
  return (
    <Form
      name="basic"
      labelCol={{
        span: 6,
      }}
      wrapperCol={{
        span: 18,
      }}
      initialValues={{
        remember: true,
      }}
      onFinish={handleSubmitLogin}
    >
      <Form.Item
        label="Username"
        name="username"
        rules={[
          {
            required: true,
            message: 'Please input your username!',
          },
          {
            max: 100,
            message: 'Username maximum 100 characters',
          },
          {
            pattern: /^[a-zA-Z0-9]*$/,
            message: 'Username just contains letters and numbers',
          },
        ]}
      >
        <Input name="username" onChange={(event) => handleChangeInput(event)} />
      </Form.Item>

      <Form.Item
        label="Password"
        name="password"
        rules={[
          {
            required: true,
            message: 'Please input your password!',
          },
        ]}
      >
        <Input.Password name="password" onChange={(event) => handleChangeInput(event)} />
      </Form.Item>

      <Form.Item
        wrapperCol={{
          offset: 6,
          span: 18,
        }}
      >
        <Button
          disabled={isSending}
          style={{ background: '#cf2338', borderColor: '#cf2338' }}
          type="primary"
          htmlType="submit"
        >
          Submit
        </Button>
      </Form.Item>
    </Form>
  );
};
export default FormLogin;
