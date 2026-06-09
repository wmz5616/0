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
        //类型
        const { dispatch, record } = this.props;

    

        //场馆
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
          },
          url: `/api/admin/gym/lists`,
          method: 'GET',
          myData: (res) => {
            if (res && res.code === 200) {
              this.setState({
                lists: res.data.lists,
              });
            } else {
              message.error(res.message);
            }
          },
        });

        if (record) {

          this.props.dispatch({
            type: 'myModel/getSetData',
            payload: {
              gym_id: record.gym_id,
              limit: 999,
            },
            url: `/api/admin/gym/site/lists`,
            method: 'GET',
            myData: (res) => {
              console.log(res);
              this.setState({
                spinning: false,
              });
              if (res && res.code === 200) {
                this.setState({
                  listxx: res.data.lists,
                });
              } else {
                message.error(res.message);
                // this.setState({ isSelectForm: true });
              }
            },
          });

          this.formRef.current.setFieldsValue({
            gym_id: record.gym_id,
            site_id: record.site_id,
            date: moment(record.date, 'YYYY-MM-DD'),
            start_time: moment(record.start_time, 'HH:mm'),
            end_time: moment(record.end_time, 'HH:mm'),
            reason: record.reason,
          });
        }
      },
    );
  };

  handleOk = () => {
    const { handleCancel, dispatch, id, getData, add,record } = this.props;

    console.log(id);
    this.formRef.current.validateFields().then((values) => {
      if (add) {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            gym_id: values.gym_id,
            site_id: values.site_id,
            date: values.date.format('YYYY-MM-DD'),
            start_time: values.start_time.format('HH:mm'), //开始时间
            end_time: values.end_time.format('HH:mm'), //结束时间
            reason: values.reason, //备注
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/gym/site/disable/add`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleCancel();
              getData();
            } else {
              message.error(res.message);
            }
          },
        });
      } else {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: record.id,
            gym_id: values.gym_id,
            site_id: values.site_id,
            date: values.date.format('YYYY-MM-DD'),
            start_time: values.start_time.format('HH:mm'), //开始时间
            end_time: values.end_time.format('HH:mm'), //结束时间
            reason: values.reason, //备注
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/gym/site/disable/update`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleCancel();
              getData();
            } else {
              message.error(res.message);
            }
          },
        });
      }
    });
  };

  handleCancel = () => {
    const { handleCancel } = this.props;
    handleCancel();
  };

  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  handleChange = (value) => {
    if (value) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          gym_id: value,
          limit: 999,
        },
        url: `/api/admin/gym/site/lists`,
        method: 'GET',
        myData: (res) => {
          console.log(res);
          this.setState({
            spinning: false,
          });
          if (res && res.code === 200) {
            this.setState({
              listxx: res.data.lists,
            });
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  render() {
    const { records } = this.props;

    const { colorsList = [], lists = [], listxx = [] } = this.state;

    return (
      <>
        <Modal
          title={`${records ? '编辑' : '新增'}`}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={800}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item label="场馆" name="gym_id" rules={[{ required: true, message: '请选择!' }]}>
              <Select
                allowClear
                showSearch
                optionFilterProp="label"
                onChange={this.handleChange}
                placeholder="请选择"
              >
                {lists.map((res) => {
                  return (
                    <Option value={res.id} key={res.name} label={`${res.id}${res.name}`}>
                      {res.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item label="场地" name="site_id" rules={[{ required: true, message: '请选择!' }]}>
              <Select allowClear showSearch optionFilterProp="label" placeholder="请选择">
                {listxx.map((res) => {
                  return (
                    <Option value={res.id} key={res.name} label={`${res.id}${res.name}`}>
                      {res.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item label="日期" name="date" rules={[{ required: true, message: '请选择!' }]}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item
              label="开放时间"
              name="start_time"
              rules={[{ required: true, message: '请选择开始时间' }]}
            >
              <TimePicker type="time" format="HH:mm" minuteStep={30} />
            </Form.Item>

            <Form.Item
              name="end_time"
              rules={[{ required: true, message: '请选择结束时间' }]}
              style={{ position: 'absolute', marginLeft: '280px', marginTop: '-56px' }}
            >
              <TimePicker type="time" format="HH:mm" style={{ width: 150 }} minuteStep={30} />
            </Form.Item>

            <div style={{ position: 'absolute', marginTop: '-50px', marginLeft: '265px' }}>~</div>

            <Form.Item label="原因" name="reason" placeholder="请输入" rules={[{ required: true }]}>
              <TextArea rows={4} />
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default connect((allModels) => ({}))(App);
