import { post } from '@/utils/request';
import { Form, Input, Modal, Radio, Select, message } from 'antd';
import React from 'react';
const { TextArea } = Input;
const { Option } = Select;
class App extends React.Component {
  formRef = React.createRef();
  state = {
    xxxx: 1,
    placeLists: [],
    shoplist: [],
  };

  componentDidMount() {
    this.getData();
  }

  getData = async () => {
    //角色列表

    const { edit, dispatch } = this.props;
    console.log(edit);
    if (!this.props.add) {
      this.formRef.current.setFieldsValue({
        roleIds: edit.roles ? edit.roles.map((a) => a.id) : undefined,
        status: edit.status,
        password: edit.password,
        name: edit.name,
        account: edit.account,
        shopIds:
          edit.shops && edit.shops != 0 ? edit.shops.map((sx) => sx.id) : [],
      });
    }
    const res = await post(`/guzhe/role/lists`, {
      pageSize: 999,
    });
    this.setState({
      spinning: false,
    });
    if (res && res.code === 10000) {
      this.setState({
        placeLists: res.data.list,
      });
    } else {
      message.error(res?.msg);
    }
  };

  handleOk = () => {
    const { handleOk, getData, add, edit } = this.props;
    this.formRef.current.validateFields().then(async (values) => {
      if (add) {
        const res = await post(`/guzhe/admin/add`, {
          roleIds: values.roleIds,
          status: values.status,
          password: values.password,
          name: values.name,
          account: values.account,
          shopIds: values.shopIds,
        });
        if (res && res.code === 10000) {
          message.success(res.msg);
          handleOk();
          getData();
        } else {
          message.error(res?.msg);
        }
      } else {
        const res = await post(`/guzhe/admin/update`, {
          roleIds: values.roleIds,
          status: values.status,
          password: values.password,
          name: values.name,
          account: values.account,
          id: edit.id,
          shopIds: values.shopIds,
        });
        if (res && res.code === 10000) {
          message.success(res.msg);
          handleOk();
          getData();
        } else {
          message.error(res?.msg);
        }
      }
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  onChange = (e) => {
    this.setState({
      xxxx: e.target.value,
    });
  };

  render() {
    const { add } = this.props;
    return (
      <>
        <Modal
          title={add ? '新增管理员' : '编辑管理员'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Form
            ref={this.formRef}
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 16 }}
          >
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>手机号
                </span>
              }
            >
              <Form.Item
                name="account"
                noStyle
                rules={[{ required: true, message: '请输入' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
              <span style={{ color: '#ccc' }}>手机号唯一，作为登录账号</span>
            </Form.Item>

            <Form.Item label="姓名" name="name" rules={[{ required: true }]}>
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item label="角色" name="roleIds" rules={[{ required: true }]}>
              <Select
                showSearch
                mode="multiple"
                placeholder="请选择"
                optionFilterProp="label"
                onChange={this.onChangep}
              >
                {this.state.placeLists.map((res) => {
                  return (
                    <Option
                      value={res.id}
                      key={res.id}
                      label={`${res.id}${res.name}`}
                    >
                      {res.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>
            <Form.Item label={<span>密码</span>}>
              <Form.Item
                name="password"
                noStyle
                rules={[
                  {
                    required: this.state.isAdd ? true : false,
                    message: '请输入密码',
                  },
                ]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
              <div style={{ color: '#ccc' }}>
                6~20位，仅支持数字、字母和下划线
              </div>
            </Form.Item>
            <Form.Item
              label="是否启用"
              name="status"
              rules={[{ required: true }]}
              initialValue={1}
            >
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

export default App;
