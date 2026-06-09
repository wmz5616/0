import React from 'react';
import { Modal, Form, Input, Select, Radio, DatePicker, Upload, message, InputNumber } from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { history, connect, Link } from 'umi';
import moment from 'moment';
import CKEditor from 'react-ckeditor-wrapper';
const { Option } = Select;
const { RangePicker } = DatePicker;
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};
class App extends React.Component {
  formRef = React.createRef();
  state = {
    pois: [],
    loading: false,
    xxxx: true,
    scheduling: 1,
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    const { info } = this.props;
    console.log(info);

    //场馆列表
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {},
      url: `/api/admin/top_up/act/gymLists`,
      method: 'GET',
      myData: (res) => {
        if (res && res.code === 200) {
          console.log(res.data);
          this.setState({
            gymLists: res.data,
          });
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });

    if (info) {
      console.log(info.gym_id);

      this.formRef.current.setFieldsValue({
        name: info.name,
        amount: info.amount,
        give_amount: info.give_amount,
        gym_id: info.gym_id[0] == '0' ? undefined : info.gym_id.map((res) => Number(res)),
      });
    }
  };

  handleOk = () => {
    const { handleOk, dispatch, getData, info } = this.props;
    this.formRef.current.validateFields().then((values) => {
      console.log(values);

      if (info) {
        //编辑
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: info.id,
            name: values.name,
            amount: values.amount,
            give_amount: values.give_amount,
            gym_id: values.gym_id,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/top_up/act/update`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleOk();
              getData();
            } else {
              message.error(res.message);
            }
          },
        });
      } else {
        //新建
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            name: values.name,
            amount: values.amount,
            give_amount: values.give_amount,
            gym_id: values.gym_id,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/top_up/act/add`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleOk();
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
    const { handleOk } = this.props;
    handleOk();
  };

  render() {
    const { add, useStatus } = this.props;

    const { imageUrl, loading, gymLists = [] } = this.state;

    return (
      <>
        <Modal
          title={add ? '编辑充值活动' : '新增充值活动'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={800}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item
              label="活动名称"
              name="name"
              rules={[{ required: true, message: '请输入!' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>充值金额
                </span>
              }
            >
              <Form.Item name="amount" noStyle rules={[{ required: true, message: '请输入!' }]}>
                <InputNumber min={0} />
              </Form.Item>
              <span style={{ marginLeft: 10 }}>元</span>
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>赠送金额
                </span>
              }
            >
              <Form.Item
                name="give_amount"
                noStyle
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber min={0} />
              </Form.Item>
              <span style={{ marginLeft: 10 }}>元</span>
            </Form.Item>

            {useStatus ? (
              <Form.Item label="指定使用场馆" name="gym_id">
                <Select
                  mode="multiple"
                  allowClear
                  showSearch
                  placeholder="请选择"
                  optionFilterProp="label"
                >
                  {gymLists.map((res) => {
                    return (
                      <Option value={res.id} key={res.id}>
                        {res.name}
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
            ) : (
              ''
            )}
          </Form>
        </Modal>
      </>
    );
  }
}
export default connect()(App);
