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
import CKEditor from 'react-ckeditor-wrapper';
class App extends React.Component {
  formRef = React.createRef();
  state = {
    loading: false,
    statusa: 0,
    statusaa: 2,
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
        //类型
        const { dispatch, records } = this.props;

        if (records) {
          console.log(records);
          this.setState({
            statusa: records.lock == 0 ? 0 : 3,
            statusaa: records.lock == 0 ? 2 : records.lock,
          });
          this.formRef.current.setFieldsValue({
            status: records.lock == 2 || records.lock == 1 ? 3 : 0, //
            statuss: records.lock, //
            lockExpiredAt:
              records.lockExpiredAt == null
                ? undefined
                : records.lockExpiredAt && records.lockExpiredAt == '0000-00-00 00:00:00'
                ? undefined
                : moment(records.lockExpiredAt, 'YYYY-MM-DD HH:mm'), //开始时间
            lockReason: records.lockReason,
            remark: records.remark,
          });
        }
      },
    );
  };

  handleOk = () => {
    const { handleOk, dispatch, ids, getData, records } = this.props;

    console.log(this.state.statusaa);
    this.formRef.current.validateFields().then((values) => {
      dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids,
          lock: this.state.statusa == 0 ? this.state.statusa : this.state.statusaa, //
          phone: values.phone,
          lockExpiredAt:
            this.state.statusaa == 1
              ? values.lock_expired_at.format('YYYY-MM-DD HH:mm')
              : undefined, //时间
          remark: values.remark ? values.remark : undefined,
          lockReason: values.lockReason,
        },
        // dataName: 'developerListData',
        method: 'POST',
        url: `/ddql/user/update`,
        myData: (res) => {
          if (res.code === 10000) {
            message.success(res.msg);
            handleOk();
            getData();
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  onChange = (e) => {
    this.setState(
      {
        statusa: e.target.value,
        statusaa: e.target.value == 0 ? 2 : this.state.statusaa,
      },
      () => {
        const { records } = this.props;
        if (records) {
          this.setState({
            statusaa: 2,
          });
          this.formRef.current.setFieldsValue({
            statuss: 2, //
            lock_expired_at:
              records.lock_expired_at == null
                ? undefined
                : records.lock_expired_at && records.lock_expired_at == '0000-00-00 00:00:00'
                ? undefined
                : moment(records.lock_expired_at, 'YYYY-MM-DD HH:mm'), //开始时间
          });
        }
      },
    );
  };

  onChanges = (e) => {
    this.setState({
      statusaa: e.target.value,
    });
  };

  render() {
    const { ids } = this.props;

    const { colorsList = [] } = this.state;

    return (
      <>
        <Modal
          title={`编辑状态`}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={600}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>状态
                </span>
              }
            >
              <Form.Item
                name="status"
                noStyle
                rules={[{ required: true, message: '请输入!' }]}
                initialValue={0}
              >
                <Radio.Group onChange={this.onChange}>
                  <Radio value={0}>正常</Radio>
                  <Radio value={3}>锁定</Radio>
                </Radio.Group>
              </Form.Item>
              <div style={{ color: '#ccc' }}>被锁定的账号无法订场或订票</div>
            </Form.Item>

            {this.state.statusa == 3 && (
              <>
                <Form.Item
                  label="锁定时效"
                  name="statuss"
                  rules={[{ required: true }]}
                  initialValue={2}
                >
                  <Radio.Group onChange={this.onChanges}>
                    <Radio value={2}>永久</Radio>
                    <Radio value={1}>有限时间</Radio>
                  </Radio.Group>
                </Form.Item>
                {this.state.statusaa == 1 && (
                  <Form.Item
                    label="解锁时间"
                    name="lockExpiredAt"
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <DatePicker
                      showTime={{ format: 'HH:mm' }}
                      format="YYYY-MM-DD HH:mm"
                      style={{ width: '100%' }}
                    />
                  </Form.Item>
                )}
              </>
            )}
            {this.state.statusa == 3 && (
              <Form.Item
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>锁定原因
                  </span>
                }
              >
                <Form.Item
                  name="lockReason"
                  noStyle
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
                <div style={{ color: '#ccc' }}>前台用户可见</div>
              </Form.Item>
            )}

            <Form.Item label={<span>备注</span>}>
              <Form.Item name="remark" noStyle>
                <TextArea rows={4} placeholder="请输入" />
              </Form.Item>
              <div style={{ color: '#ccc' }}>仅后台管理员可见</div>
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default connect((allModels) => ({}))(App);
