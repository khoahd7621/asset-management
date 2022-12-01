import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Button, Form, Modal, Input } from 'antd';

import './FirstPasswordModal.scss';

import { putChangePasswordFirst } from '../../services/editApiService';
import { updateFirstLogin } from '../../redux/slice/userSlice';

const FirstPasswordModal = () => {
  const [form] = Form.useForm();
  const initError = {
    help: '',
    status: '',
  };

  const newPasswordType = Form.useWatch('newPassword', form);

  const user = useSelector((state) => state.user);
  const dispatch = useDispatch();
  const [isSending, setIsSending] = useState(true);
  const [newPasswordValidate, setNewPasswordValidate] = useState({ ...initError });
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    if (user.isFirstLogin) {
      setIsModalOpen(true);
    }
  }, []);
  useEffect(() => {
    if (!newPasswordType || newPasswordValidate.help) {
      setIsSending(true);
      return;
    }
    setIsSending(false);
  }, [newPasswordType]);

  const handleValidString = (event, type) => {
    if (event.target.value.trim().length === 0) {
      if (type === 'NEW_PASSWORD') {
        setNewPasswordValidate({
          help: '',
          status: 'error',
        });
        return;
      }
    }
    if (type === 'NEW_PASSWORD') {
      setNewPasswordValidate({ ...initError });
    }
  };

  const handleSubmitChange = async () => {
    const value = form.getFieldsValue();
    const response = await putChangePasswordFirst({
      newPassword: value.newPassword,
    });
    if (response && response.status === 200) {
      form.resetFields();
      setIsModalOpen(false);
      setNewPasswordValidate({
        initError,
      });
      dispatch(updateFirstLogin());
    } else {
      setNewPasswordValidate({
        help: '',
        status: 'error',
      });
    }
  };

  return (
    <Modal
      className="modal-change-password-wapper"
      open={isModalOpen}
      closable={false}
      okButtonProps={{
        style: {
          display: 'none',
        },
      }}
      cancelButtonProps={{ style: { display: 'none' } }}
      footer={null}
    >
      <div>
        <h3 className="modal-title">Change Password</h3>
      </div>
      <hr />
      <div className="modal-content">
        <div>
          This is the first time you logged in.
          <p>You have to change your password to continue.</p>
        </div>
        <Form
          form={form}
          initialValues={{
            remember: true,
          }}
          onFinish={handleSubmitChange}
          className="change-password-form"
        >
          <Form.Item
            label="New Password"
            name="newPassword"
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
              status={newPasswordValidate.status}
              name="password"
              onChange={(event) => handleValidString(event, 'NEW_PASSWORD')}
            />
          </Form.Item>
          <Button type="primary" htmlType="submit" disabled={isSending}>
            Save
          </Button>
        </Form>
      </div>
    </Modal>
  );
};
export default FirstPasswordModal;
