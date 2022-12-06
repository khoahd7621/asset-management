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
  const [form] = Form.useForm();
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

  useEffect(() => {
    if (!user.username || !user.password) {
      setIsSending(true);
    } else {
      setIsSending(false);
    }
  }, [user.username, user.password]);

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
      return;
    }
    if (!Validation.isMaxLength(user.password, 24)) {
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
    } else {
      toast.error(response?.response?.data?.message);
    }
    setIsSending(false);
  };
  return (
    <Form
      form={form}
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
            message: '',
          },
          {
            pattern: /^[a-zA-Z0-9]*$/,
            message: '',
          },
        ]}
      >
        <Input name="username" onChange={(event) => handleChangeInput(event)} id="username" />
      </Form.Item>

      <Form.Item
        label="Password"
        name="password"
        rules={[
          {
            required: true,
            message: '',
          },
        ]}
      >
        <Input.Password name="password" onChange={(event) => handleChangeInput(event)} id="password" />
      </Form.Item>

      <Form.Item
        wrapperCol={{
          offset: 6,
          span: 18,
        }}
      >
        <Button disabled={isSending} htmlType="submit" className='login-button'>
          Login
        </Button>
      </Form.Item>
    </Form>
  );
};
export default FormLogin;
