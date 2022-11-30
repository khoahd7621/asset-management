import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { Dropdown, Layout, Space, Button, Form, Input, Menu } from 'antd';
import { CaretDownOutlined } from '@ant-design/icons';
import { removeDataUserLogout } from '../../redux/slice/userSlice';

import './Navbar.scss';
import CustomModal from '../Modal/Modal';

import CustomBreadcrumb from './CustomBreadcrumb';
import { adminRoute, userRoute } from '../../routes/routes';
import { putChangePassword } from '../../services/editApiService';

const Navbar = () => {
  const location = useLocation();
  const { Header } = Layout;
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

  const [form] = Form.useForm();
  const initError = {
    help: '',
    status: '',
  };

  const [openModalChangePassword, setOpenModalChangePassword] = useState(false);
  const [openChangeSuccess, setOpenChangeSuccess] = useState(false);
  const [newPasswordValidate, setNewPasswordValidate] = useState({ ...initError });
  const newPasswordType = Form.useWatch('newPassword', form);
  const oldPasswordType = Form.useWatch('oldPassword', form);
  const [isSending, setIsSending] = useState(true);
  const dispatch = useDispatch();

  const [openModalLogout, setOpenModalLogout] = useState(false);
  const handleLogout = () => {
    dispatch(removeDataUserLogout());
  };

  const handleSelectLogout = () => {
    setOpenModalLogout(true);
  };

  const handleChangePassword = () => {
    setOpenModalChangePassword(true);
  };

  const [oldPasswordValidator, setOldPasswordValidator] = useState({ ...initError });

  useEffect(() => {
    if (!newPasswordType || !oldPasswordType || newPasswordValidate.help || oldPasswordValidator.help) {
      setIsSending(true);
      return;
    }
    setIsSending(false);
  }, [newPasswordType, oldPasswordType]);

  const handleValidString = (event, type) => {
    if (event.target.value.trim().length === 0) {
      if (type === 'NEW_PASSWORD') {
        setNewPasswordValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'OLD_PASSWORD') {
        setOldPasswordValidator({
          help: '',
          status: 'error',
        });
        return;
      }
    }
    if (type === 'NEW_PASSWORD') {
      setNewPasswordValidate({ ...initError });
    }
    if (type === 'OLD_PASSWORD') {
      setOldPasswordValidator({ ...initError });
    }
  };

  const handleCancel = () => {
    form.resetFields();
    setOldPasswordValidator({ ...initError });
    setNewPasswordValidate({ ...initError });
    setOpenModalChangePassword(false);
  };

  const handleCancelWhenChangeSuccess = () => {
    setOpenChangeSuccess(false);
  };

  const handleSubmitChange = async () => {
    const value = form.getFieldsValue();
    const response = await putChangePassword({
      oldPassword: value.oldPassword,
      newPassword: value.newPassword,
    });
    if (response && response.status === 200) {
      form.resetFields();
      setOpenModalChangePassword(false);
      setOldPasswordValidator({
        initError,
      });
      setOpenChangeSuccess(true);
    } else {
      setOldPasswordValidator({
        help: '',
        status: 'error',
      });
    }
  };

  const menu = (
    <Menu>
      <Menu.Item onClick={handleChangePassword}>Change Password</Menu.Item>
      <Menu.Item onClick={handleSelectLogout}>Logout</Menu.Item>
    </Menu>
  );

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
        <Dropdown overlay={menu} trigger={['click']}>
          <a onClick={(e) => e.preventDefault()}>
            <Space>
              {user.username} <CaretDownOutlined />
            </Space>
          </a>
        </Dropdown>
      </div>
      <CustomModal
        className="modal-asset-detail"
        title="Are you sure?"
        open={openModalLogout}
        closable={false}
        onCancel={() => {}}
        width="270px"
      >
        <p>Do you want to log out?</p>

        <Button onClick={handleLogout} danger type="primary" htmlType="submit">
          Log out
        </Button>
        <Button style={{ marginLeft: '15px' }} htmlType="submit">
          Cancel
        </Button>
      </CustomModal>

      <CustomModal
        className="modal-asset-detail"
        title="Change Password"
        open={openModalChangePassword}
        closable={false}
        onCancel={() => {}}
        width="550px"
      >
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
            oldPassword: '',
            newPassword: '',
          }}
          onFinish={() => {}}
          className="change-password-form"
        >
          <Form.Item
            label="Old Password"
            name="oldPassword"
            colon={false}
            rules={[
              {
                required: false,
                message: '',
              },
            ]}
            help={oldPasswordValidator.help}
          >
            <Input.Password
              id="oldPassword"
              name="password"
              status={oldPasswordValidator.status}
              onChange={(event) => handleValidString(event, 'OLD_PASSWORD')}
            />
          </Form.Item>

          <Form.Item
            label="New Password"
            name="newPassword"
            colon={false}
            rules={[
              {
                required: false,
                message: '',
              },
            ]}
            help={newPasswordValidate.help}
          >
            <Input.Password
              id="newPassword"
              name="password"
              status={newPasswordValidate.status}
              onChange={(event) => handleValidString(event, 'NEW_PASSWORD')}
            />
          </Form.Item>
        </Form>
        <div className="btnChangePassword">
          <Button onClick={handleSubmitChange} disabled={isSending} danger type="primary" htmlType="submit">
            Save
          </Button>
          <Button onClick={handleCancel} style={{ marginLeft: '15px' }} htmlType="submit">
            Cancel
          </Button>
        </div>
      </CustomModal>

      <CustomModal
        className="modal-asset-detail"
        title="Change Password"
        open={openChangeSuccess}
        closable={false}
        onCancel={() => {}}
        width="400px"
      >
        <p>Your password has been changed successfully!</p>
        <div className="btnChangePassword">
          <Button onClick={handleCancelWhenChangeSuccess} style={{ marginRight: '0px' }} htmlType="submit">
            Close
          </Button>
        </div>
      </CustomModal>
    </Header>
  );
};

export default Navbar;
