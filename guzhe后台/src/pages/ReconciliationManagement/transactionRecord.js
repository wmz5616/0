import {
  exportTransactionFlow,
  getTransactionFlowList,
  getTransactionFlowSummary,
} from '@/services/transaction';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Alert,
  Button,
  Col,
  DatePicker,
  Dropdown,
  Form,
  Input,
  Row,
  Select,
  Space,
  Table,
  Typography,
} from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { useLocation } from 'umi';

const { Option } = Select;
const { RangePicker } = DatePicker;
const { Text } = Typography;

const TransactionRecord = () => {
  const [form] = Form.useForm();
  const location = useLocation();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const initialState = location.state || {};
  const [searchParams, setSearchParams] = useState({
    startTime:
      initialState.startTime ||
      dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
    endTime:
      initialState.endTime ||
      dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
    pageNum: 1,
    pageSize: 10,
  });

  const [listData, setListData] = useState({ list: [], total: 0, summary: {} });

  const [loading, setLoading] = useState(false);

  const loadData = async (params) => {
    setLoading(true);
    try {
      const combined = { ...searchParams, ...params };
      const cleanParams = {};
      Object.keys(combined).forEach((key) => {
        const val = combined[key];
        if (val !== undefined && val !== null && val !== '') {
          cleanParams[key] = typeof val === 'string' ? val.trim() : val;
        }
      });

      const [listRes, summaryRes] = await Promise.all([
        getTransactionFlowList(cleanParams).catch(() => ({ code: 500 })),
        getTransactionFlowSummary(cleanParams).catch(() => ({ code: 500 })),
      ]);

      const listData =
        listRes?.code === 10000 || listRes?.code === 200
          ? Array.isArray(listRes.data)
            ? listRes.data
            : listRes.data?.list || listRes.data?.records || []
          : [];
      const totalCount =
        listRes?.code === 10000 || listRes?.code === 200
          ? listRes.data?.total || listData.length
          : 0;
      const finalData = {
        list: listData,
        total: totalCount,
        summary:
          summaryRes?.code === 10000 || summaryRes?.code === 200
            ? summaryRes.data || {}
            : {},
      };

      console.log('强制同步状态数据:', finalData);
      setListData(finalData);
    } catch (error) {
      console.error('加载流水数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const targetParams =
      initialState?.startTime && initialState?.endTime
        ? {
            ...searchParams,
            startTime: initialState.startTime,
            endTime: initialState.endTime,
            pageNum: 1,
          }
        : searchParams;

    if (initialState?.startTime) {
      form.setFieldsValue({
        time: [dayjs(initialState.startTime), dayjs(initialState.endTime)],
      });
      setSearchParams(targetParams);
    }

    loadData(targetParams);
  }, []);

  const list = listData.list;
  const total = listData.total;
  const summary = listData.summary;

  const onFinish = (values) => {
    const { time, ...rest } = values;
    const newParams = {
      ...rest,
      startTime: time ? time[0].format('YYYY-MM-DD 00:00:00') : undefined,
      endTime: time ? time[1].format('YYYY-MM-DD 23:59:59') : undefined,
      pageNum: 1,
      pageSize: searchParams.pageSize || 10,
    };
    setSearchParams(newParams);
    loadData(newParams);
  };

  const handleReset = () => {
    form.resetFields();
    setSelectedRowKeys([]);
    const defaultParams = {
      startTime: dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
      endTime: dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
      pageNum: 1,
      pageSize: 10,
    };
    setSearchParams(defaultParams);
    loadData(defaultParams);
  };

  const handleExport = (type) => {
    const params = { ...searchParams };
    if (type === 'selected') {
      if (selectedRowKeys.length === 0) {
        return;
      }
      params.searchStrList = selectedRowKeys;
    }

    exportTransactionFlow(params).then((res) => {
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      const objectUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = objectUrl;
      a.download = `交易流水_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(objectUrl);
    });
  };

  const columns = [
    {
      title: '交易时间',
      dataIndex: 'transactionTime',
      key: 'transactionTime',
      width: 170,
    },
    {
      title: '交易订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 200,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (text) => (
        <Text type={text === '收款' ? 'success' : 'danger'}>{text}</Text>
      ),
    },
    {
      title: '手续费（元）',
      dataIndex: 'serviceFee',
      key: 'serviceFee',
      width: 120,
      render: (val) =>
        val !== null && val !== undefined ? `￥${Number(val).toFixed(2)}` : '-',
    },
    {
      title: '收入（元）',
      dataIndex: 'income',
      key: 'income',
      width: 120,
      render: (val) =>
        val !== null && val !== undefined ? (
          <Text type="success">￥{Number(val).toFixed(2)}</Text>
        ) : (
          '-'
        ),
    },
    {
      title: '支出（元）',
      dataIndex: 'expense',
      key: 'expense',
      width: 120,
      render: (val) =>
        val !== null && val !== undefined ? (
          <Text type="danger">￥{Number(val).toFixed(2)}</Text>
        ) : (
          '-'
        ),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 200,
      ellipsis: true,
    },
    {
      title: '商户订单号',
      dataIndex: 'merchantOrderNo',
      key: 'merchantOrderNo',
      width: 200,
    },
  ];

  const menuProps = {
    items: [
      {
        key: 'all',
        label: '导出全部',
        onClick: () => handleExport('all'),
      },
      {
        key: 'selected',
        label: '导出选中',
        onClick: () => handleExport('selected'),
      },
    ],
  };

  return (
    <PageContainer title={false}>
      <div
        style={{
          backgroundColor: '#fff',
          padding: '20px 20px 0 20px',
          marginBottom: 15,
        }}
      >
        <Form form={form} onFinish={onFinish}>
          <Row gutter={16}>
            <Col className="gutter-row" span={5}>
              <Form.Item
                label="交易时间"
                name="time"
                initialValue={[dayjs().startOf('month'), dayjs()]}
              >
                <RangePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="交易订单号" name="orderNo">
                <Input placeholder="请输入交易订单号" allowClear />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={3}>
              <Form.Item label="类型" name="type">
                <Select
                  placeholder="请选择类型"
                  allowClear
                  style={{ width: '100%' }}
                >
                  <Option value="1">收款</Option>
                  <Option value="2">退款</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="商户订单号" name="merchantOrderNo">
                <Input placeholder="请输入商户订单号" allowClear />
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
                <Button className="mL15" onClick={handleReset}>
                  重置
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>

      <div style={{ backgroundColor: '#fff', padding: 20 }}>
        <Row gutter={16}>
          <Col span={6}>
            <h1 style={{ fontWeight: '600', fontSize: '18px' }}>交易流水</h1>
          </Col>
          <Col
            span={18}
            style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}
          >
            <Dropdown menu={menuProps} placement="bottomRight">
              <Button type="primary">导出</Button>
            </Dropdown>
          </Col>
          <Col span={24} style={{ margin: '10px 0' }}>
            <Alert
              message={
                <Space split={<span>|</span>} size="middle">
                  <span>
                    查询统计：共{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      {summary.totalCount || 0}
                    </Text>{' '}
                    笔
                  </span>
                  <span>
                    收入{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      {summary.incomeCount || 0}
                    </Text>{' '}
                    笔， 金额{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      ￥{(summary.incomeAmount || 0).toFixed(2)}
                    </Text>
                  </span>
                  <span>
                    支出{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      {summary.expenseCount || 0}
                    </Text>{' '}
                    笔， 金额{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      ￥{(summary.expenseAmount || 0).toFixed(2)}
                    </Text>
                  </span>
                  <span>
                    手续费金额{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      ￥{(summary.totalServiceFee || 0).toFixed(2)}
                    </Text>
                  </span>
                </Space>
              }
              type="info"
              showIcon
            />
          </Col>
        </Row>

        <Table
          style={{ marginTop: 15 }}
          rowSelection={{
            selectedRowKeys,
            onChange: setSelectedRowKeys,
          }}
          rowKey="id"
          columns={columns}
          dataSource={list}
          pagination={{
            showSizeChanger: true,
            pageSize: searchParams.pageSize || 10,
            total,
            current: searchParams.pageNum || 1,
            onChange: (page, pageSize) => {
              const newParams = { ...searchParams, pageNum: page, pageSize };
              setSearchParams(newParams);
              loadData(newParams);
            },
          }}
          loading={loading}
          scroll={{ x: 'max-content' }}
        />
      </div>
    </PageContainer>
  );
};

export default TransactionRecord;
