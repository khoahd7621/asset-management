import Modal from '../Modal/Modal';
import { deleteAsset } from '../../services/disableApiService';

const ModalAssetConfirmation = ({
  open,
  onCancel,
  data,
  searchKeywords,
  statuses,
  categories,
  fetchListAssets,
  pageSize,
  handleChangePage,
}) => {
  const handleSubmitDeleteAsset = async () => {
    const response = await deleteAsset(data);
    if (response && response.status === 204) {
      handleChangePage(1);
      onCancel();
      fetchListAssets(searchKeywords, statuses, categories, pageSize, 0, '', '');
    } else {
      console.log(response?.response?.message);
    }
  };

  return (
    <Modal className="modal-asset-confirmation" title={'Are you sure?'} open={open} closable={false} width="350px">
      <div className="content">Do you want to delete this asset?</div>
      <div className="action">
        <button className="delete" onClick={() => handleSubmitDeleteAsset()}>
          Delete
        </button>
        <button className="cancel" onClick={() => onCancel()}>
          Cancel
        </button>
      </div>
    </Modal>
  );
};

export default ModalAssetConfirmation;
