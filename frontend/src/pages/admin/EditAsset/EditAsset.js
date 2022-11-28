import { Button, DatePicker, Divider, Form, Input, Radio, Select, Space } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { putEditAsset } from '../../../services/editApiService';
import './EditAsset.scss';
import { adminRoute } from '../../../routes/routes';
import { getAssetDetailAndItsHistories } from '../../../services/getApiService';
import moment from 'moment';

const EditAsset = () => {
  const navigate = useNavigate();
  const createAccountlayout = {
    labelCol: {
      span: 6,
    },
    wrapperCol: {
      span: 20,
    },
  };

  const { TextArea } = Input;

  const [form] = Form.useForm();

  const params = useParams();

  const initialError = { help: '', status: '' };

  const assetNameType = Form.useWatch('assetName', form);
  const specificationType = Form.useWatch('specification', form);
  const stateType = Form.useWatch('assetStatus', form);
  const installedDateType = Form.useWatch('installedDate', form);

  const [isDisabled, setIsDisabled] = useState(true);

  const [assetNameValidate, setAssetNameValidate] = useState({ ...initialError });

  const [installedDateValidate, setInstalledDateValidate] = useState({ ...initialError });
  const [specificationValidate, setSpecificationValidate] = useState({ ...initialError });
  const [assetInformation, setAssetInformation] = useState({});
  const [category, setCategory] = useState();

  useEffect(() => {
    if (
      assetNameValidate.help ||
      installedDateValidate.help ||
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
    form.setFieldsValue({
      assetName: assetInformation.assetName,
      assetStatus: assetInformation.status,
      installedDate: moment(assetInformation.installedDate),
      specification: assetInformation.specification,
    });
    setCategory(assetInformation?.category?.name);
  }, [assetInformation, form]);

  const fetchGetAssetById = async () => {
    const response = await getAssetDetailAndItsHistories(params.id);
    if (response && response.status === 200) {
      setAssetInformation(response.data.asset);
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
          help: 'Asset name is required',
          status: 'error',
        });
        return;
      }
      if (type === 'SPECIFICATION') {
        setSpecificationValidate({
          help: 'Specification is required',
          status: 'error',
        });
        return;
      }
    }
    if (event.target.value.trim().length > 100) {
      if (type === 'ASSET_NAME') {
        setAssetNameValidate({
          help: 'Maximum 100 characters',
          status: 'error',
        });
        return;
      }
    }
    if (event.target.value.trim().length > 500) {
      if (type === 'SPECIFICATION') {
        setSpecificationValidate({
          help: 'Maximum 500 characters',
          status: 'error',
        });
        return;
      }
    }
    if (type === 'ASSET_NAME') {
      if (!/^([a-zA-Z0-9]+\s)*[a-zA-Z0-9]+$/.test(event.target.value)) {
        setAssetNameValidate({
          help: 'First name only accepts not contains any special characters and number.',
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
      <div className="edit-asset-block">
        <h3 className="edit-asset__title">Edit Asset</h3>
        <Form
          {...createAccountlayout}
          className="edit-asset__form"
          form={form}
          name="control-hooks"
          onFinish={handleSubmitEditAsset}
        >
          <Form.Item name="assetName" label="Name" help={assetNameValidate.help}>
            <Input
              id="edit-asset-input__asset-name"
              onChange={(event) => handleValidString(event, 'ASSET_NAME')}
              status={assetNameValidate.status}
            />
          </Form.Item>
          <Form.Item name="category" label="Category">
            <Select id="edit-asset-select__type" placeholder={category} allowClear value={'LD'} disabled></Select>
          </Form.Item>
          <Form.Item name="specification" label="Specification" help={specificationValidate.help}>
            <TextArea
              rows={4}
              placeholder=""
              maxLength={200}
              status={specificationValidate.status}
              onChange={(event) => handleValidString(event, 'SPECIFICATION')}
              style={{ maxHeight: '7rem', minHeight: '7rem' }}
            />
          </Form.Item>
          <Form.Item name="installedDate" label="Installed Date" help={installedDateValidate.help}>
            <DatePicker
              id="edit-asset-date-picker__joined-date"
              placeholder=""
              format={['DD/MM/YYYY', 'D/MM/YYYY', 'D/M/YYYY', 'DD/M/YYYY']}
              // onChange={handleValidateJoinedDate}
              status={installedDateValidate.status}
            />
          </Form.Item>
          <Form.Item name="assetStatus" label="State">
            <Radio.Group id="edit-asset-radio__gender" value={stateType} style={{ paddingTop: '0.4rem' }}>
              <Space direction="vertical">
                <Radio value={'AVAILABLE'}>Available</Radio>
                <Radio value={'NOT_AVAILABLE'}>Not Available</Radio>
                <Radio value={'WAITING_FOR_RECYCLING'}>Waiting for recycling</Radio>
                <Radio value={'RECYCLED'}>Recycled</Radio>
              </Space>
            </Radio.Group>
          </Form.Item>
          <div className="edit-asset__form-action">
            <Button type="primary" danger htmlType="submit" disabled={isDisabled}>
              Save
            </Button>
            <Button htmlType="button" onClick={handleClickClose}>
              Cancel
            </Button>
          </div>
        </Form>
      </div>
    </>
  );
};
export default EditAsset;