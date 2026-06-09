import React from 'react';
import { Modal, Form, Input, Select, Radio, message } from 'antd';
import { history, connect } from 'umi';
const { TextArea } = Input;
const { Option } = Select;
class App extends React.Component {
  formRef = React.createRef();
  state = {
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    //角色列表

    const { edit, dispatch } = this.props;
    if (!this.props.add) {
      this.formRef.current.setFieldsValue({
        name: edit.name,
        status: edit.status,
        remark: edit.remark,
      });
    }
  };

  handleOk = () => {
    const { handleOk, getData, add, edit } = this.props;

    this.formRef.current.validateFields().then((values) => {
      if (add) {
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            name: values.name,
            type: values.type,
            status: values.status ? 1 : 0,
            remark: values.remark,
          },
          url: `/ddql/role/add`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              message.success(res.msg);
              handleOk();
              getData();
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      } else {
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            name: values.name,
            type: values.type,
            status: values.status ? 1 : 0,
            remark: values.remark,
            id: edit.id,
          },
          url: `/ddql/role/update`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              message.success(res.msg);
              handleOk();
              getData();
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
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
    const { add } = this.props;
    return (
      <>
        <Modal
          style={{ minWidth: '38%' }}
          title={add ? '新增角色' : '编辑角色'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Form ref={this.formRef} labelCol={{ span: 4 }}>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>名称
                </span>
              }
            >
              <Form.Item name="name" noStyle rules={[{ required: true, message: '请输入' }]}>
                <Input placeholder="请输入" />
              </Form.Item>
              <div style={{ color: '#ccc', marginTop: 10, marginBottom: -10 }}>角色名称唯一</div>
            </Form.Item>
            {/* <Form.Item  label="角色类型" name="type" rules={[{ required: true, message: '请选择' }]}>
                <Select placeholder="请选择">
                  <Option value={1}>普通用户</Option>
                  <Option value={2}>管理员</Option>
                  <Option value={3}>超级管理员</Option>
                </Select>
              </Form.Item> */}
            <Form.Item label="说明" name="remark">
              <TextArea rows={4} placeholder="请输入" />
            </Form.Item>

            <Form.Item label="是否启用" name="status" rules={[{ required: true }]} initialValue={1}>
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default connect()(App);
