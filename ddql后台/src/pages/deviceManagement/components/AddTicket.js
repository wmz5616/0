import React from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  Radio,
  DatePicker,
  Upload,
  Row,
  Col,
  TimePicker,
  InputNumber,
  message,
} from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { connect } from 'umi';
import moment from 'moment';
const { TextArea } = Input;
const { Option } = Select;
const { RangePicker } = DatePicker;
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};
class App extends React.Component {
  formRef = React.createRef();
  state = {
    loading: false,
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        const { dispatch, id, edit } = this.props;
        //管理员
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
          },
          url: `/api/admin/member/lists`,
          method: 'GET',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.setState({
                listss: res.data.lists,
              });
            } else {
              message.error(res.message);
            }
          },
        });

        if (edit) {
          console.log(edit.admins);
          this.formRef.current.setFieldsValue({
            device_num: edit.device_num,
            device_name:edit.device_name,
            admin_ids: edit.admins && edit.admins.map((res) => res.admin && res.admin.id),
            enable: edit.enable,
            remark: edit.remark,
          });
        }

        console.log(edit);
      },
    );
  };

  handleOk = () => {
    const { handleOk, dispatch, id, getData, add, edit } = this.props;

    if (add) {
      this.formRef.current.validateFields().then((values) => {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            device_name: values.device_name,
            device_type: 2,
            device_num: values.device_num, //设备序列号
            enable: values.enable,
            remark: values.remark,
            admin_ids: values.admin_ids.join(','),
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/device/add`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleOk();
              getData();
            }else{
              message.error(res.message);
            }
          },
        });
      });
    } else {
      this.formRef.current.validateFields().then((values) => {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            device_name: values.device_name,
            device_type: 2,
            device_num: values.device_num, //设备序列号
            admin_ids: values.admin_ids.join(','),
            enable: values.enable,
            remark: values.remark,
            id: edit.id,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/device/update`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleOk();
              getData();
            }else{
              message.error(res.message);
            }
          },
        });
      });
    }
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  render() {
    const { add } = this.props;

    const { listss = [] } = this.state;

    return (
      <>
        <Modal
          title={add ? '新增设备' : '编辑设备'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>设备名称
                </span>
              }
            >
              <Form.Item
                name="device_name"
                noStyle
                rules={[{ required: true, message: '请输入!' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
              <div style={{ color: '#ccc' }}>设备名称唯一</div>
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>管理员
                </span>
              }
            >
              <Form.Item name="admin_ids" noStyle rules={[{ required: true, message: '请选择!' }]}>
                <Select
                  mode="multiple"
                  allowClear
                  showSearch
                  placeholder="请选择"
                  optionFilterProp="label"
                >
                  {listss.map((res) => {
                    return (
                      <Option value={res.id} key={res.id} label={`${res.username}${res.phone}`}>
                        {res.username}（ {res.phone}）
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
              <div style={{ color: '#ccc' }}>仅授权管理员可使用该设备打印小票</div>
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>终端号
                </span>
              }
            >
              <Form.Item name="device_num" noStyle rules={[{ required: true, message: '请输入!' }]}>
                <Input placeholder="请输入" />
              </Form.Item>
              <div style={{ color: '#ccc' }}>终端号唯一</div>
            </Form.Item>

            <Form.Item label="是否启用" name="enable" rules={[{ required: true }]} initialValue={1}>
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>

            <Form.Item label="备注" name="remark" rules={[{ required: true }]}>
              <TextArea rows={4} placeholder="请输入" />
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default connect((allModels) => ({}))(App);
