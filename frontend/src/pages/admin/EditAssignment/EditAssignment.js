import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, DatePicker, Form, Input, Space, Spin } from 'antd';
import TextArea from 'antd/lib/input/TextArea';
import moment from 'moment';
import { CaretDownOutlined } from '@ant-design/icons';

import './EditAssignment.scss';

import { ModalChooseUser, ModalChooseAsset } from '../../../components';
import { adminRoute } from '../../../routes/routes';
import { getAssignmentDetails } from '../../../services/getApiService';
import { putEditAssignment } from '../../../services/editApiService';

const EditAssignment = () => {
  const formLayout = {
    labelCol: {
      span: 6,
    },
    wrapperCol: {
      span: 20,
    },
  };
  const [form] = Form.useForm();
  const initialError = { help: '', status: '' };

  const navigate = useNavigate();
  const params = useParams();

  const [currentUser, setCurrentUser] = useState({
    userId: 0,
    fullName: '',
  });
  const [currentAsset, setCurrentAsset] = useState({
    assetId: 0,
    assetName: '',
  });
  const [assignedDate, setAssignedDate] = useState(moment());
  const [note, setNote] = useState('');
  const [isShowModalUser, setIsShowModalUser] = useState(false);
  const [isShowModalAsset, setIsShowModalAsset] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [assetValidate, setAssetValidate] = useState({ ...initialError });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    document.title = 'Manage Assignment - Edit Assignment';
    fetchAssignmentById();
  }, []);

  const fetchAssignmentById = async () => {
    const response = await getAssignmentDetails(params.id);
    if (response && response.status === 200) {
      if (response?.data?.status === 'WAITING_FOR_ACCEPTANCE') {
        setCurrentUser({
          userId: response?.data?.userAssignedToId,
          fullName: response?.data?.userAssignedToFullName,
        });
        setCurrentAsset({
          assetId: response?.data?.assetId,
          assetName: response?.data?.assetName,
        });
        setAssignedDate(moment(response?.data?.assignedDate));
        setNote(response?.data?.note ?? '');
      } else {
        navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}`);
      }
    } else {
      navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}`);
    }
    setIsLoading(false);
  };

  const handleSaveChoose = (type, data) => {
    if (type === 'USER') {
      setCurrentUser({ ...data });
      form.setFieldValue('fullName', data.fullName);
      setIsShowModalUser(false);
    }
    if (type === 'ASSET') {
      setCurrentAsset({ ...data });
      form.setFieldValue('assetName', data.assetName);
      setIsShowModalAsset(false);
    }
  };

  const handleSubmitEditAssignment = async () => {
    setIsSending(true);
    const payload = {
      assetId: currentAsset.assetId,
      userId: currentUser.userId,
      assignedDate: assignedDate.format('DD/MM/YYYY'),
      note,
    };
    const response = await putEditAssignment(params.id, payload);
    if (response && response.status === 200) {
      navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}`, {
        state: {
          assignmentResponse: response.data,
        },
      });
    } else {
      if (
        response?.response?.data?.message === 'Can only assign asset with status available.' ||
        response?.response?.data?.message === 'Not exist asset with this asset id.'
      ) {
        setAssetValidate({
          help: 'Maybe asset has been deleted or status has changed. Please reload page to update list assets.',
          status: 'error',
        });
      }
    }
    setIsSending(false);
  };

  return (
    <>
      {isLoading ? (
        <Space size="middle">
          <Spin size="large" style={{ paddingLeft: '30rem' }} />
        </Space>
      ) : (
        <div className="edit-assignment-block">
          <h3 className="edit-assignment__title">Edit Assignment</h3>
          <Form
            {...formLayout}
            initialValues={{
              fullName: currentUser.fullName,
              assetName: currentAsset.assetName,
              assignedDate: assignedDate,
              note: note,
            }}
            className="edit-assignment__form"
            form={form}
            colon={false}
            onFinish={() => {}}
          >
            <Form.Item name="fullName" label="User" labelAlign="left">
              <Input
                id="edit-assignment-input__full-name"
                readOnly
                suffix={
                  <span className="suffix-icon" onClick={() => setIsShowModalUser(true)}>
                    <CaretDownOutlined />
                  </span>
                }
              />
            </Form.Item>
            <Form.Item name="assetName" label="Asset" labelAlign="left" help={assetValidate.help}>
              <Input
                id="edit-assignment-input__asset-name"
                readOnly
                suffix={
                  <span className="suffix-icon" onClick={() => setIsShowModalAsset(true)}>
                    <CaretDownOutlined />
                  </span>
                }
                status={assetValidate.status}
              />
            </Form.Item>
            <Form.Item name="assignedDate" label="Assigned Date" labelAlign="left">
              <DatePicker
                id="edit-assignment-date-picker__assigned-date"
                placeholder="dd/mm/yyyy"
                format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
                onChange={(date, _dateStr) => {
                  setAssignedDate(date);
                }}
                disabledDate={(current) => {
                  let customDate = moment().format('DD/MM/YYYY');
                  return (
                    (current && current < moment(customDate, 'DD/MM/YYYY')) ||
                    current > moment('01/01/9999', 'DD/MM/YYYY')
                  );
                }}
              />
            </Form.Item>
            <Form.Item name="note" label="Note" labelAlign="left">
              <TextArea
                rows={4}
                id="edit-assignment-input__note"
                style={{
                  height: 120,
                  resize: 'none',
                }}
                maxLength={500}
                onChange={(event) => {
                  setNote(event.target.value);
                }}
              />
            </Form.Item>
            <div className="edit-assignment__form-action">
              <Button
                type="primary"
                htmlType="submit"
                className="save-button"
                disabled={!currentUser.userId || !currentAsset.assetId || !assignedDate || isSending}
                onClick={handleSubmitEditAssignment}
              >
                Save
              </Button>
              <Button htmlType="button" onClick={() => navigate(`/${adminRoute.home}/${adminRoute.manageAssignment}`)}>
                Cancel
              </Button>
            </div>
          </Form>
          <ModalChooseUser
            open={isShowModalUser}
            onCancel={() => setIsShowModalUser(false)}
            currentUser={currentUser}
            handleSaveChoose={handleSaveChoose}
          />
          <ModalChooseAsset
            open={isShowModalAsset}
            onCancel={() => setIsShowModalAsset(false)}
            currentAsset={currentAsset}
            handleSaveChoose={handleSaveChoose}
          />
        </div>
      )}
    </>
  );
};

export default EditAssignment;
