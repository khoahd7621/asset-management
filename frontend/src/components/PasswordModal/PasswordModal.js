import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Button, Form, Modal, Input } from 'antd';

import './PasswordModal.scss';

const PasswordModal = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const user = useSelector((state) => state.user.user);

  useEffect(() => {
    if (user.isFirstLogin) {
      setIsModalOpen(true);
    }
  }, []);

  const handleOk = () => {
    setIsModalOpen(false);
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
        <Form>
          <Form.Item
            label="New Password"
            name="password"
            rules={[
              {
                required: false,
                message: 'Please input your password!',
              },
            ]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
        <Button
          style={{ background: '#cf2338', borderColor: '#cf2338' }}
          type="primary"
          htmlType="submit"
          onClick={handleOk}
        >
          Save
        </Button>
      </div>
    </Modal>
  );
};
export default PasswordModal;
