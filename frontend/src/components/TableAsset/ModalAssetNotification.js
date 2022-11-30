import { Link } from 'react-router-dom';

import { adminRoute } from '../../routes/routes';
import Modal from '../Modal/Modal';

const ModalAssetNotification = ({ open, onCancel, data }) => {
  return (
    <Modal className="modal-asset-notification" title={'Cannot Delete Asset'} open={open} onCancel={onCancel}>
      <div className="content">
        <div>Cannot delete the asset because it belongs to one or more historical assignments.</div>
        <div>
          If the asset is not able to be used anymore, please update its state in{' '}
          <Link to={`/${adminRoute.home}/${adminRoute.manageAsset}/${adminRoute.editAsset}/${data}`}>
            Edit Asset page
          </Link>
        </div>
      </div>
    </Modal>
  );
};

export default ModalAssetNotification;
