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

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            group: 'receipts_print',
          },
          url: `/api/admin/system/config`,
          method: 'GET',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              const object = {};
              Object.keys(res.data).map((resd) => {
                object[res.data[resd].name] = res.data[resd].value;
              });
              this.setState(
                {
                  listss: res.data,
                  display: res.data.filter((res) => res.name === 'receipts_unify_phone')[0].value
                    ? 2
                    : 1,
                },
                () => {
                  this.formRef.current.setFieldsValue({
                    enable: res.data.filter((res) => res.name === 'receipts_unify_phone')[0].value
                      ? 2
                      : 1,
                  });
                },
              );

              this.formRef.current.setFieldsValue(object);
            } else {
              message.error(res.message);
            }
          },
        });

        // if (edit) {
        //   console.log(edit.admins);
        //   this.formRef.current.setFieldsValue({
        //     device_num: edit.device_num,
        //     device_name: edit.device_name,
        //     admin_ids: edit.admins && edit.admins.map((res) => res.admin && res.admin.id),
        //     enable: edit.enable,
        //     remark: edit.remark,
        //   });
        // }

        console.log(edit);
      },
    );
  };

  handleOk = () => {
    const { handleOk, dispatch, id, getData, add, edit } = this.props;
    console.log(111);
    this.formRef.current.validateFields().then((values) => {
      console.log(222);
      dispatch({
        type: 'myModel/getSetData',
        payload: {
          group: 'receipts_print',
          receipts_title: values.receipts_title,
          receipts_unify_phone: values.enable == 1 ? '' : values.receipts_unify_phone,
          receipts_order_tips: values.receipts_order_tips,
          receipts_ticket_tips: values.receipts_ticket_tips,
        },
        // dataName: 'developerListData',
        method: 'POST',
        url: `/api/admin/system/config`,
        myData: (res) => {
          console.log(res)
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
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  onChange = (e) => {
    this.setState({
      display: e.target.value,
    });
  };

  render() {
    const { listss = [], display } = this.state;

    return (
      <>
        <Modal title="打印模板设置" visible onOk={this.handleOk} onCancel={this.handleCancel}>
          <Form ref={this.formRef} {...layout}>
            <Form.Item
              label="小票标题"
              name="receipts_title"
              rules={[{ required: true, message: '请输入!' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item label="客服电话" name="enable" rules={[{ required: true }]} initialValue={1}>
              <Radio.Group onChange={this.onChange}>
                <Radio value={1}>各场馆客服电话</Radio>
                <Radio value={2}>统一客服电话</Radio>
              </Radio.Group>
            </Form.Item>

            {display == 2 && (
              <Form.Item
                wrapperCol={{ offset: 4, span: 18 }}
                name="receipts_unify_phone"
                rules={[{ required: true, message: '请输入!' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            )}

            <Form.Item label="订单提示" name="receipts_order_tips">
              <TextArea rows={4} placeholder="请输入" />
            </Form.Item>

            <Form.Item label="票券提示" name="receipts_ticket_tips">
              <TextArea rows={4} placeholder="请输入" />
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default connect((allModels) => ({}))(App);
