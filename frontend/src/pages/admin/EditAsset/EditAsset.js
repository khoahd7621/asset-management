import { useEffect, useState } from 'react';
import { Button, DatePicker, Form, Input, Radio, Select, Space, Spin } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import moment from 'moment';

import './EditAsset.scss';

import { putEditAsset } from '../../../services/editApiService';
import { adminRoute } from '../../../routes/routes';
import { getAssetDetailAndItsHistories } from '../../../services/getApiService';

const EditAsset = () => {
  const formLayout = {
    labelCol: {
      span: 6,
    },
    wrapperCol: {
      span: 20,
    },
  };
  const { TextArea } = Input;

  const navigate = useNavigate();
  const [form] = Form.useForm();
  const params = useParams();

  const initialError = { help: '', status: '' };

  const assetNameType = Form.useWatch('assetName', form);
  const specificationType = Form.useWatch('specification', form);
  const stateType = Form.useWatch('assetStatus', form);
  const installedDateType = Form.useWatch('installedDate', form);

  const [isDisabled, setIsDisabled] = useState(true);
  const [assetNameValidate, setAssetNameValidate] = useState({ ...initialError });
  const [specificationValidate, setSpecificationValidate] = useState({ ...initialError });
  const [assetInformation, setAssetInformation] = useState({});
  const [category, setCategory] = useState();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    document.title = 'Manage Asset - Edit Asset';
  }, []);

  useEffect(() => {
    if (
      assetNameValidate.help ||
      specificationValidate.help ||
      !assetNameType ||
      !stateType ||
      !installedDateType ||
      !specificationType
    ) {
      setIsDisabled(true);
      return;
    }
    setIsDisabled(false);
  }, [assetNameType, stateType, installedDateType, specificationType]);

  useEffect(() => {
    fetchGetAssetById();
  }, []);

  useEffect(() => {
    if (assetInformation.status == 'ASSIGNED') handleClickClose();
    if (assetInformation?.status) setIsLoading(false);
  }, [assetInformation]);

  useEffect(() => {
    form.setFieldsValue({
      assetName: assetInformation.assetName,
      assetStatus: assetInformation.status,
      installedDate: moment(assetInformation.installedDate),
      specification: assetInformation.specification,
    });
    setCategory(assetInformation?.category?.name);
  }, [assetInformation, form]);

  const fetchGetAssetById = async () => {
    setIsLoading(true);
    const response = await getAssetDetailAndItsHistories(params.id);
    if (response && response.status === 200) {
      setAssetInformation(response.data.asset);
    } else {
      navigate(`/${adminRoute.home}/${adminRoute.manageAsset}`);
    }
  };

  const handleClickClose = () => {
    form.resetFields();
    navigate(`/${adminRoute.home}/${adminRoute.manageAsset}`);
  };

  const handleSubmitEditAsset = async (values) => {
    setIsDisabled(true);
    const response = await putEditAsset({
      ...values,
      id: params.id,
      assetName: values.assetName.trim(),
      installedDate: values.installedDate.format('DD/MM/YYYY'),
    });
    if (response && response.status === 200) {
      form.resetFields();
      navigate(`/${adminRoute.home}/${adminRoute.manageAsset}`, {
        state: {
          assetResponse: response.data,
        },
      });
    }
    setIsDisabled(false);
  };

  const handleValidString = (event, type) => {
    if (event.target.value.trim().length === 0) {
      if (type === 'ASSET_NAME') {
        setAssetNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      if (type === 'SPECIFICATION') {
        setSpecificationValidate({
          help: '',
          status: 'error',
        });
        return;
      }
    }

    if (type === 'ASSET_NAME') {
      if (!/^([A-Za-z0-9\s])*[A-Za-z0-9\s]+$/.test(event.target.value)) {
        setAssetNameValidate({
          help: '',
          status: 'error',
        });
        return;
      }
      setAssetNameValidate({ ...initialError });
    }
    if (type === 'SPECIFICATION') {
      setSpecificationValidate({ ...initialError });
    }
  };

  return (
    <>
      {isLoading ? (
        <Space size="middle">
          <Spin size="large" style={{ paddingLeft: '30rem' }} />
        </Space>
      ) : (
        <div className="edit-asset-block">
          <h3 className="edit-asset__title">Edit Asset</h3>
          <Form
            {...formLayout}
            className="edit-asset__form"
            form={form}
            name="control-hooks"
            onFinish={handleSubmitEditAsset}
          >
            <Form.Item name="assetName" label="Name" help={assetNameValidate.help} colon={false}>
              <Input
                id="edit-asset-input__asset-name"
                className="edit-asset-input__asset-name"
                onChange={(event) => handleValidString(event, 'ASSET_NAME')}
                status={assetNameValidate.status}
                maxLength={100}
              />
            </Form.Item>
            <Form.Item name="category" label="Category" colon={false}>
              <Select
                id="edit-asset-select__type"
                placeholder={category}
                allowClear
                value={'LD'}
                disabled
              ></Select>
            </Form.Item>
            <Form.Item name="specification" label="Specification" help={specificationValidate.help} colon={false}>
              <TextArea
                id="edit-asset-specification"
                className='edit-asset-specification'
                rows={4}
                placeholder=""
                maxLength={200}
                status={specificationValidate.status}
                onChange={(event) => handleValidString(event, 'SPECIFICATION')}
                style={{ maxHeight: '7rem', minHeight: '7rem' }}
              />
            </Form.Item>
            <Form.Item name="installedDate" label="Installed Date" colon={false}>
              <DatePicker
                id="edit-asset-date-picker__joined-date"
                placeholder=""
                format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
              />
            </Form.Item>
            <Form.Item name="assetStatus" label="State" colon={false}>
              <Radio.Group id="edit-asset-radio__gender" value={stateType} style={{ paddingTop: '0.4rem' }}>
                <Space direction="vertical">
                  <Radio value={'AVAILABLE'}>Available</Radio>
                  <Radio value={'NOT_AVAILABLE'}>Not available</Radio>
                  <Radio value={'WAITING_FOR_RECYCLING'}>Waiting for recycling</Radio>
                  <Radio value={'RECYCLED'}>Recycled</Radio>
                </Space>
              </Radio.Group>
            </Form.Item>
            <div className="edit-asset__form-action">
              <Button className="edit-asset-button" htmlType="submit" disabled={isDisabled}>
                Save
              </Button>
              <Button className="cancel-edit-asset-button" htmlType="button" onClick={handleClickClose}>
                Cancel
              </Button>
            </div>
          </Form>
        </div>
      )}
    </>
  );
};
export default EditAsset;
