import { Modal } from 'antd';

import { deleteRequestForReturning } from '../../services/disableApiService';

const ModalRequestCanceled = ({
  open,
  onCancel,
  data,
  searchKeywords,
  statuses,
  date,
  fetchListRequest,
  handleChangePage,
}) => {
  const handleSubmitCancelRequest = async () => {
    const response = await deleteRequestForReturning(data);
    if (response && response.status === 204) {
      handleChangePage(1);
      onCancel();
      fetchListRequest(searchKeywords, statuses, date, 0);
    }
  };
  return (
    <Modal
      open={open}
      className="user-list__disable-modal"
      title={'Are you sure?'}
      centered
      onOk={() => handleSubmitCancelRequest()}
      onCancel={() => onCancel()}
      okText="Yes"
      cancelText="No"
      closable={false}
    >
      <p>Do you want to cancel this returning request?</p>
    </Modal>
  );
};

export default ModalRequestCanceled;
