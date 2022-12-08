import { Modal } from 'antd';

import { patchCompleteRequestReturnAsset } from '../../services/editApiService';

const ModalRequestCompleted = ({
  open,
  onCancel,
  data,
  searchKeywords,
  statuses,
  date,
  fetchListRequest,
  handleChangePage,
}) => {
  const handleSubmitCompleteRequest = async () => {
    const response = await patchCompleteRequestReturnAsset(data);
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
      onOk={() => handleSubmitCompleteRequest()}
      onCancel={() => onCancel()}
      okText="Yes"
      cancelText="No"
      closable={false}
    >
      <p>Do you want to mark this returning request as 'Completed'?</p>
    </Modal>
  );
};

export default ModalRequestCompleted;
