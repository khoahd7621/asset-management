import { Modal } from 'antd';
import { CloseSquareOutlined } from '@ant-design/icons';

import './Modal.scss';

const CustomModal = ({
  className = '',
  title = 'Custom modal',
  centered = true,
  open = false,
  onCancel = () => {},
  width = '500px',
  closable = true,
  children,
  ...props
}) => {
  return (
    <Modal
      className={`custom-modal ${className}`}
      title={title}
      centered={centered}
      open={open}
      onCancel={onCancel}
      closeIcon={<CloseSquareOutlined />}
      footer={null}
      width={width}
      mask={false}
      closable={closable}
      {...props}
    >
      {children}
    </Modal>
  );
};

export default CustomModal;
