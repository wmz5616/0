import React from 'react';
import {
  UserOutlined,
  LockOutlined,
  PlusSquareOutlined,
  MinusSquareOutlined,
} from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Table,
  Select,
  Radio,
  Switch,
  Modal,
  InputNumber,
  Alert,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect } from 'umi';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
// import { setToken } from '@/utils/authority';
const { Option } = Select;

class RoleManagement extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    editModalVisible: false,
    serviceLists: [],
    selectedRowKeys: [],
    ccc: true,
    departmentId: null,
    showDepartment: false,
    departmentList: [],
  };

  componentDidMount() {
    this.getDepartment();
    this.getData();
  }

  getDepartment = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: Number(this.props.info.id),
      },
      url: `/ddql/team/get/dept`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            departmentList: res.data,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { serviceLists } = this.state;
    const dragRow = serviceLists[dragIndex];
    console.log(hoverIndex);

    this.setState(
      update(this.state, {
        serviceLists: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
      () => {
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchIds: this.state.serviceLists.map((as) => as.id),
          },
          url: `/ddql/shop/org/type/sort`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              this.getService();
              message.success(res.msg);
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        //角色列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            teamId: Number(this.props.info.id),
            keyword: this.state.keyword,
            userName: this.state.userName,
            userPhone: this.state.userPhone,
            type: this.state.type,
            status: this.state.status,
            pageNum: this.state.pageNum,
            departmentId: this.state.departmentId,
            // pageSize: 10,
          },
          url: `/ddql/team/selectTeamUser`,
          method: 'POST',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 10000) {
              this.setState({
                list: res.data.list,
                total: res.data.total,
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({ selectedRowKeys });
  };
  handleCancel = () => {
    this.setState({
      addGroupModalVisible: false,
    });
  };
  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
      },
      () => {
        this.getData();
      },
    );
  };
  editSubmit = () => {
    this.formRef.current.validateFields().then((values) => {
      const params = {
        teamId: this.props.id,
        userId: this.state.id,
        userName: values.userName,
        userPhone: values.userPhone,
        departmentId: values.department,
        type: values.type,
      };
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: { ...params },
        url: this.state.isAdd ? '/ddql/team/user/add' : '/ddql/team/updateTeamUser',
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.setState(
              {
                editModalVisible: false,
              },
              () => {
                this.getData();
                this.getDepartment();
              },
            );
          } else {
            message.info(res.msg);
          }
        },
      });
    });
  };
  submitDepartment = () => {
    const { departmentList } = this.state;
    const teamId = this.props.info.id;

    if (departmentList.some(dept => !dept.name || !dept.name.trim())) {
      message.error("部门名称不能为空");
      return;
    }

    const promises = departmentList.map((dept, index) => {
      if (dept.id) {
        return new Promise((resolve) => {
          this.props.dispatch({
            type: 'myModel/getSetData',
            payload: { id: dept.id, name: dept.name, teamId: Number(teamId), sort: index },
            url: '/ddql/team/department/update',
            method: 'POST',
            myData: (res) => resolve(res)
          });
        });
      } else {
        return new Promise((resolve) => {
          this.props.dispatch({
            type: 'myModel/getSetData',
            payload: { name: dept.name, teamId: Number(teamId), sort: index },
            url: '/ddql/team/department/add',
            method: 'POST',
            myData: (res) => resolve(res)
          });
        });
      }
    });

    Promise.all(promises).then((results) => {
      const hasError = results.some(res => !res || res.code !== 10000);
      if (hasError) {
         message.error("部分部门保存失败");
      } else {
         message.success("部门保存成功");
         this.setState({ showDepartment: false });
         this.getDepartment();
      }
    });
  };
  editModal = (e, record) => {
    const datas = [];
    if (record.introUrl) {
      record.introUrl.split(',').map((resd, index) => {
        datas.push({
          uid: index.toString(),
          status: 'done',
          url: urlName + resd,
          response: { data: { url: resd } },
        });
      });
    }
    this.setState(
      {
        editModalVisible: true,
        isAdd: false,
        id: record.id,
        videoUrl: record.videoUrl,
        imageUrls: record.logoUrl,
        status: record.status,
        fileList: datas,
      },
      () => {
        setTimeout(() => {
          this.formRefs.current.setFieldsValue({
            name: record.name,
            videoUrl: true,
            logoUrl: true,
            introUrl: true,
          });
        }, 150);
      },
    );
  };

  // 删除函数
  //删除
  deletes = (ids, e) => {
    if (e.type == 0) {
      message.info('不允许删除创建者');
      return;
    }
    if (ids.length == 0) {
      message.error('请选择要删除的数据');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          deleteIds: ids,
        },
        url: `/ddql/team/user/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getData();
            this.getDepartment();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  onFinish = (v) => {
    console.log(v);
    this.setState(
      {
        keyword: v.keyword,
        userName: v.userName,
        userPhone: v.userPhone,
        type: v.type,
        status: v.status,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };
  showDepartmentModal = () => {
    let list = [...this.state.departmentList];
    if (list.length === 0) {
      list.push({ sort: 1 });
    }
    this.setState({
      showDepartment: true,
      departmentList: list,
    });
  };
  addDepartment = (index) => {
    const data = [...this.state.departmentList];
    data.splice(index + 1, 0, { sort: index + 1 });
    this.setState({ departmentList: data });
  };
  deleteDepartment = (index) => {
    const data = [...this.state.departmentList];
    const dept = data[index];
    if (dept && dept.count > 0) {
      message.warning('含有成员的部门不允许删除');
      return;
    }
    const id = dept?.id;
    // 判断删除的项是否含有id
    if (id) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchId: id,
        },
        url: `/ddql/team/department/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getDepartment()

          } else {
            message.error(res.msg);
          }
        },
      });
      return;
    }
    data.splice(index, 1);
    this.setState({ departmentList: data });
  };
  showModal = (record) => {
    this.setState(
      {
        editModalVisible: true,
        id: record ? record.userId : null,
        isAdd: !record,
        editType: false,
      },
      () => {
        if (record) {
          setTimeout((_) => {
            this.formRef.current.setFieldsValue({
              userName: record.userName,
              userPhone: record.userPhone,
              department: record.departmentId,
              type: record.type,
            });
          }, 100);
        } else {
          setTimeout((_) => {
            this.formRef.current.resetFields();
            this.formRef.current.setFieldsValue({
              type: 2,
            });
          }, 100);
        }
      },
    );
  };
  resets = () => {
    this.formRefs.current.resetFields();
    this.setState(
      {
        keyword: undefined,
        userName: undefined,
        userPhone: undefined,
        type: undefined,
        status: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  submitService = () => {
    const params = {
      orgTypeList: this.state.serviceLists,
    };
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/shop/org/type/add`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({ addGroupModalVisible: false });
          message.success(res.msg);
          this.getService();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  addGroup = () => {
    const data = JSON.parse(JSON.stringify(this.state.serviceLists));
    data.push({ sort: data.length + 1, shopId: Number(this.props.id) });
    this.setState({ serviceLists: data });
  };

  front = (checked, record) => {
    //启用状态修改
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {
        changeIds: [record.id],
        status: checked ? 0 : 1,
      },
      method: 'POST',
      url: `/ddql/team/user/status/set`,
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else if (res) {
          message.error(res.msg || res.message);
        }
      },
    });
  };

  render() {
    const { list = [], selectedRowKeys, NewRoles, pageNum, total } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
    const typeColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '类型名称',
        dataIndex: 'name',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                data[index].name = e.target.value;
                this.setState({
                  serviceLists: data,
                });
              }}
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '操作',
        render: (res, record, index) => (
          <div>
            <span
              className="mL15 red"
              onClick={() => {
                if (record.id) {
                  this.props.dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      deleteId: record.id,
                    },
                    url: `/ddql/shop/org/type/delete`,
                    method: 'POST',
                    myData: (res) => {
                      if (res && res.code === 10000) {
                        message.success(res.msg);
                        this.getService();
                      } else {
                        message.error(res.msg);
                        // this.setState({ isSelectForm: true });
                      }
                    },
                  });
                } else {
                  const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                  data.splice(index, 1);
                  data.map((resd, index) => {
                    resd.sort = index + 1;
                  });
                  this.setState({
                    serviceLists: data,
                  });
                }
              }}
            >
              删除
            </span>
          </div>
        ),
      },
    ];
    const columns = [
      {
        title: '序号',
        dataIndex: 'id',
      },
      {
        title: '头像',
        dataIndex: 'avatar',
        render: (text, record) => {
          return (
            <>
              <img src={text} alt="" style={{ width: 103.5, height: 37.5, objectFit: 'contain' }} />
            </>
          );
        },
      },
      {
        title: '真实姓名',
        dataIndex: 'userName',
      },
      {
        title: '小程序昵称',
        dataIndex: 'name',
      },
      {
        title: '手机号',
        dataIndex: 'userPhone',
      },
      {
        title: '成员身份',
        dataIndex: 'type',
        render: (res) => <div>{res == 0 ? '创建者' : res == 1 ? '管理员' : '普通用户'}</div>,
      },
      this.props.info?.isMultiDepartment === 1 ? {
        title: '部门',
        dataIndex: 'departmentName',
      } : null,
      {
        title: '加入方式',
        dataIndex: 'joinType',
        render: (res) => <div>{res == 1 ? '扫码加入' : '申请加入'}</div>,
      },
      {
        title: '启用状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <Switch
              checkedChildren="开启"
              unCheckedChildren="关闭"
              checked={!text}
              onChange={(value) => this.front(value, record)}
            />
          );
        },
      },
      {
        title: '操作',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(record)}>编辑</a>

              <Popconfirm
                title={
                  <>
                    <div>删除提示</div>
                    <div>
                      <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                      <span style={{ color: '#ccc' }}>确定删除吗？</span>
                    </div>
                  </>
                }
                onConfirm={() => this.deletes([record.id], record)}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
              >
                <span className="mL15 red">删除</span>
              </Popconfirm>
            </div>
          );
        },
      },
    ].filter(Boolean);

    return (
      <div>
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
            <Form ref={this.formRefs} onFinish={this.onFinish}>
              <Row gutter={24}>
                <Col span={6}>
                  <Form.Item label="成员姓名" name="userName">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="手机号" name="userPhone">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="启用状态" name="status">
                    <Select placeholder="请选择" allowClear>
                      <Option value={0}>启用</Option>
                      <Option value={1}>禁用</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={24}>
                <Col span={6}>
                  <Form.Item label="成员身份" name="type">
                    <Select placeholder="请选择" allowClear>
                      <Option value={0}>创建者</Option>
                      <Option value={1}>管理员</Option>
                      <Option value={2}>普通成员</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={18} style={{ textAlign: 'right' }}>
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>
                    <Button style={{ marginLeft: 15 }} onClick={this.resets}>
                      重置
                    </Button>
                    <Button style={{ marginLeft: 15 }} onClick={() => history.goBack()}>
                      返回
                    </Button>
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row align="middle" style={{ marginBottom: 15 }}>
              <Col span={6}>
                <h1 style={{ fontWeight: '600', fontSize: '18px', margin: 0 }}>成员信息</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  {this.props.info?.isMultiDepartment === 1 && (
                    <Button type="primary" onClick={() => this.showDepartmentModal()} style={{ marginRight: 15 }}>
                      编辑部门
                    </Button>
                  )}
                  <Popconfirm
                    title={
                      <>
                        <div>删除提示</div>
                        <div>
                          <span style={{ color: 'red' }}>确定删除该成员吗？</span>
                        </div>
                      </>
                    }
                    onConfirm={() => this.deletes(selectedRowKeys, {type: -1})}
                    okText="确定"
                    cancelText="取消"
                  >
                    <Button danger>
                      删除
                    </Button>
                  </Popconfirm>
                </div>
              </Col>
            </Row>
            {this.props.info?.isMultiDepartment === 1 && (
              <Row style={{ marginBottom: 15 }}>
                <Col span={24}>
                  <div style={{ display: 'flex', gap: 30, flexWrap: 'wrap' }}>
                    <div
                      style={{
                        display: 'flex',
                        gap: 10,
                        alignItems: 'center',
                        color: this.state.departmentId === null ? '#1890ff' : '',
                        cursor: 'pointer',
                      }}
                      onClick={() => {
                        this.setState({ departmentId: null }, () => {
                          this.pageChange(1);
                        });
                      }}
                    >
                      <span>全部部门</span>
                      <div
                        style={{
                          backgroundColor: this.state.departmentId === null ? '#e6f7ff' : '#ebebeb',
                          borderRadius: '20px',
                          padding: '0px 10px',
                          color: this.state.departmentId === null ? '#1890ff' : '#666',
                        }}
                      >
                        {this.props.info?.peopleNumber || 0}
                      </div>
                    </div>
                    {this.state.departmentList.map((i) => (
                      <div
                        key={i.id}
                        style={{
                          display: 'flex',
                          gap: 10,
                          alignItems: 'center',
                          color: this.state.departmentId === i.id ? '#1890ff' : '',
                          cursor: 'pointer',
                        }}
                        onClick={() => {
                          this.setState({ departmentId: i.id }, () => {
                            this.pageChange(1);
                          });
                        }}
                      >
                        <span>{i.name}</span>
                        <div
                          style={{
                            backgroundColor: this.state.departmentId === i.id ? '#e6f7ff' : '#ebebeb',
                            borderRadius: '20px',
                            padding: '0px 10px',
                            color: this.state.departmentId === i.id ? '#1890ff' : '#666',
                          }}
                        >
                          {i.count}
                        </div>
                      </div>
                    ))}
                  </div>
                </Col>
              </Row>
            )}

            <Table
              style={{ marginTop: 15 }}
              loading={this.state.loading}
              rowKey="id"
              rowSelection={rowSelection}
              columns={columns}
              dataSource={list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: pageNum,
              }}
              // scroll={{ x: '100%' }}
            />
          </div>
        </Spin>
        <Modal
          destroyOnClose
          style={{ minWidth: '45%' }}
          onOk={this.editSubmit}
          open={this.state.editModalVisible}
          title={
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: 35 }}>
              <div style={{ width: 4, height: 20, background: '#1890ff', marginRight: 10 }}></div>
              <h3 style={{ margin: 0 }}>编辑成员</h3>
            </div>
          }
          onCancel={() => {
            this.setState({ editModalVisible: false });
          }}
        >
          <Form
            ref={this.formRef}
            labelCol={{ span: 4 }}
            wrapperCol={{ span: 16 }}
            initialValues={{
              status: true, // 默认启用状态为开启
            }}
          >
            {/* 项目名称 */}
            <Form.Item
              label="成员姓名"
              name="userName"
              rules={[{ required: false, message: '请输入项目名称!' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>
            <Form.Item
              label="手机号"
              name="userPhone"
              rules={[{ required: true, message: '请输入手机号!' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>
            <div
              style={{
                color: '#999',
                fontSize: 12,
                marginLeft: '17%',
                marginBottom: 10,
                marginTop: -20,
              }}
            >
              手机号唯一，作为小程序登录账号
            </div>
            {this.props.info?.isMultiDepartment === 1 ? (
              <Form.Item
                label="部门"
                name="department"
                rules={[{ required: false, message: '请选择部门!' }]}
              >
                <Select placeholder="请选择">
                  {this.state.departmentList.map(dept => (
                    <Option key={dept.id} value={dept.id}>{dept.name}</Option>
                  ))}
                </Select>
              </Form.Item>
            ) : (
              <Form.Item label="启用状态" name="status" valuePropName="checked">
                <Switch checkedChildren="开启" unCheckedChildren="关闭" />
              </Form.Item>
            )}
          </Form>
        </Modal>
        <Modal
          destroyOnClose
          style={{ minWidth: '30%' }}
          onOk={this.submitDepartment}
          open={this.state.showDepartment}
          title={
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: 35 }}>
              <div style={{ width: 4, height: 20, background: '#1890ff', marginRight: 10 }}></div>
              <h3 style={{ margin: 0 }}>编辑部门</h3>
            </div>
          }
          onCancel={() => {
            this.setState({ showDepartment: false });
            this.getDepartment();
          }}
        >
          <Row align="middle" style={{ gap: 20 }}>
            <Col span={20} style={{ display: 'flex' }}>
              <text>团体：</text>
              <div>{this.props.info.name}</div>
            </Col>
            <Col
              style={{
                maxHeight: 500,
                overflowY: 'auto',
                display: 'flex',
                flexDirection: 'column',
                gap: 20,
                flexGrow: 1,
              }}
            >
              {this.state.departmentList.map((i, index) => (
                <div
                  span={24}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    gap: 40,
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', flexGrow: 1 }}>
                    <text style={{ flexShrink: 0 }}>部门{index + 1}：</text>
                    <Input
                      value={i.name}
                      autoFocus={this.state.departmentList.length - 1 == index}
                      onChange={(e) => {
                        let departmentList = [...this.state.departmentList];
                        departmentList[index].name = e.target.value;
                        this.setState({ departmentList });
                      }}
                    />
                  </div>
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'flex-end',
                      gap: 20,
                      width: 84,
                    }}
                  >
                    {this.state.departmentList.length - 1 == index && (
                      <Button
                        color="primary"
                        variant="text"
                        icon={<PlusSquareOutlined />}
                        onClick={() => this.addDepartment(index)}
                      ></Button>
                    )}
                    {(this.state.departmentList.length > 1 || i.id) && (
                      <Popconfirm
                        title={
                          <>
                            <div>删除提示</div>
                            <div>
                              <span>确定删除该部门吗？</span>
                            </div>
                          </>
                        }
                        onConfirm={() => this.deleteDepartment(index)}
                        okText="是"
                        cancelText="否"
                      >
                        <Button color="primary" variant="text" icon={<MinusSquareOutlined />} />
                      </Popconfirm>
                    )}
                  </div>
                </div>
              ))}
            </Col>
          </Row>
        </Modal>
      </div>
    );
  }
}

export default connect()(RoleManagement);