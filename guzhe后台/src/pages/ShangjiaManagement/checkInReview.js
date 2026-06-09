import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Row,
  Select,
  Spin,
  Table,
} from 'antd';
import React from 'react';
import { history } from '@umijs/max';
import dayjs from 'dayjs';
const { Option } = Select;
const { RangePicker } = DatePicker;

class checkInReview extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    list: [],
    selectedRowKeys: [],
    pageNum: 1,
    www: [],
    pageSize: 10,
    total: 0,
    userList: [],
  };

  componentDidMount() {
    this.getData();
    this.getUserList();
  }

  getUserList = async () => {
    //管理员列表
    const res = await post(`/guzhe/common/admin/lists`, {
      searchIntStatus: 1,
    });
    this.setState({
      spinning: false,
    });
    if (res && res.code == 10000) {
      this.setState({
        userList: res.data || [],
      });
    } else {
      message.error(res?.msg);
    }
  };

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      async () => {
        //列表
        const res = await post(`/guzhe/shop/audit/lists`, {
          keyword: this.state.keyword,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          searchField1: this.state.searchField1,
          startTime2: this.state.startTime2,
          endTime2: this.state.endTime2,
          pageNum: this.state.pageNum,
          pageSize: 10,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          this.setState({
            list: res.data.list,
            total: res.data.total,
          });
        } else {
          message.error(res?.msg);
        }
      },
    );
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

  onFinish = (vas) => {
    this.setState(
      {
        keyword: vas.keyword,
        startTime: vas.applyTime
          ? vas.applyTime[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTime: vas.applyTime
          ? vas.applyTime[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
        searchField1: vas.searchField1,
        startTime2: vas.reviewTime
          ? vas.reviewTime[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTime2: vas.reviewTime
          ? vas.reviewTime[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        keyword: undefined,
        searchField1: undefined,
        startTime: undefined,
        endTime: undefined,
        startTime2: undefined,
        endTime2: undefined,
        selectedRowKeys: [],
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };
  g;
  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  render() {
    const { selectedRowKeys, total, pageNum } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
    const columns = [
      {
        title: '图片',
        dataIndex: 'coverImageUrl',
        render: (text, record) => {
          return (
            <>
              <img
                src={text}
                alt=""
                style={{ width: 103.5, height: 37.5, objectFit: 'contain' }}
              />
            </>
          );
        },
      },
      {
        title: '商家名称',
        dataIndex: 'name',
      },
      {
        title: '申请时间',
        dataIndex: 'submitTime',
        render: (res) => <div>{res ? dayjs(res).format('YYYY-MM-DD HH:mm:ss') : ''}</div>
      },
      {
        title: '申请人',
        dataIndex: 'userName',
        render: (res, record) => <div>{res}{record?.phone ? `(${record.phone})` : ''}</div>,
      },
      {
        title: '审核状态',
        dataIndex: 'applyResult',
        render: (res) => (
          <div
            style={{
              color:
                res == 0
                  ? '#1890ff'
                  : res == 1
                    ? '#5cd668'
                    : res == 2
                      ? '#d9011c'
                      : '',
            }}
          >
            {res == 0
              ? '待审核'
              : res == 1
                ? '已通过'
                : res == 2
                  ? '已驳回'
                  : ''}
          </div>
        ),
      },
      {
        title: '审核人员',
        dataIndex: 'auditUser',
        render: (res, record) => <div>{res ? this.state.userList.find(item => item.id == res)?.name : ''}{record?.auditPhone ? `(${record.auditPhone})` : ''}</div>,
      },
      {
        title: '审核时间',
        dataIndex: 'auditTime',
        render: (res) => <div>{res ? dayjs(res).format('YYYY-MM-DD HH:mm:ss') : ''}</div>
      },
      {
        title: '操作',
        dataIndex: '',
        render: (text, record) => {
          return (
            <>
              {record.applyResult == 0 && (
                <span
                  className="clickFont"
                  onClick={() =>
                    history.push(`/shangjiaManagement/shangjiaInfomation?id=${record.id}&auditId=${record.id}&type=review&qualificationCert
                      =${record.qualificationCert}`)
                  }
                >
                  审核
                </span>
              )}
              <span
                className="clickFont"
                style={{ marginLeft: 10 }}
                onClick={() =>
                  history.push(`/shangjiaManagement/shangjiaInfomation?id=${record.id}&type=info`)
                }
              >
                详情
              </span>
            </>
          );
        },
      },
    ];

    return (
      <PageContainer
        header={{
          title: ``,
        }}
      >
        <Spin spinning={this.state.spinning}>
          <div
            style={{
              backgroundColor: '#fff',
              padding: '20px 20px 0 20px',
              marginBottom: 15,
            }}
          >
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col
                  className="gutter-row"
                  span={4}
                  style={{ marginRight: 30 }}
                >
                  <Form.Item label="商家名称" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="申请时间" name="applyTime">
                    <RangePicker />
                  </Form.Item>
                </Col>
                <Col
                  className="gutter-row"
                  span={5}
                  style={{ marginRight: 30 }}
                >
                  <Form.Item label="审核人员" name="searchField1">
                    <Select showSearch allowClear placeholder="请选择" optionFilterProp="children">
                      {this.state.userList.map((sxsa) => (
                        <Option value={sxsa.id}>{sxsa.name}（{sxsa.account}）</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="审核时间" name="reviewTime">
                    <RangePicker />
                  </Form.Item>
                </Col>

                <Col
                  className="gutter-row"
                  style={{ textAlign: 'right', flex: '1 0 220px' }}
                >
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>

                    <Button className="mL15" onClick={this.resets}>
                      重置
                    </Button>
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row>
              <Col span={6}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  商家入驻审核
                </h1>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              rowSelection={rowSelection}
              rowKey="id"
              columns={columns}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: pageNum,
              }}
              scroll={{ x: 'max-content' }}
            />
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default checkInReview;
