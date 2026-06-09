import React, { useState, useEffect } from 'react';
import {
  Form,
  Button,
  Row,
  Col,
  Table,
  DatePicker,
  Dropdown,
  message,
  Space,
  Typography,
  Input,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { useRequest, history } from 'umi';
import dayjs from 'dayjs';
import { getTransactionSummaryList, exportTransactionSummary } from '@/services/transaction';

const { RangePicker } = DatePicker;
const { Text } = Typography;

const TransactionSummary = () => {
  const [form] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [searchParams, setSearchParams] = useState({
    startTime: dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
    endTime: dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
    pageNum: 1,
    pageSize: 10,
  });

  const [listData, setListData] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadData = async (params) => {
    setLoading(true);
    try {
      const res = await getTransactionSummaryList(params);
      console.log('加载汇总数据结果:', res);
      if (res.code === 10000 || res.code === 200) {
        setListData(res.data?.list || []);
        setTotalCount(res.data?.total || 0);
      }
    } catch (error) {
      console.error('加载汇总数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData(searchParams);
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const list = listData;
  const total = totalCount;

  const onFinish = (values) => {
    const { time } = values;
    const newParams = {
      startTime: time ? time[0].format('YYYY-MM-DD 00:00:00') : undefined,
      endTime: time ? time[1].format('YYYY-MM-DD 23:59:59') : undefined,
      pageNum: 1,
      pageSize: searchParams.pageSize,
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
      if (selectedRowKeys.length === 0) return;
      params.searchStrList = selectedRowKeys;
    }
    exportTransactionSummary(params).then((res) => {
      if (!(res instanceof Blob)) {
        message.error(res?.msg || '导出失败，无权限访问');
        return;
      }
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      const objectUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = objectUrl;
      a.download = `交易汇总_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(objectUrl);
    });
  };

  const columns = [
    {
      title: '账单日期',
      dataIndex: 'billDate',
      key: 'billDate',
      width: 140,
    },
    {
      title: '交易收入 (元)',
      dataIndex: 'transactionIncome',
      key: 'transactionIncome',
      width: 160,
      render: (val) => (val !== undefined ? `￥${Number(val).toFixed(2)}` : '-'),
    },
    {
      title: '退款支出 (元)',
      dataIndex: 'refundExpense',
      key: 'refundExpense',
      width: 160,
      render: (val) => (
        <Text type="danger">{val !== undefined ? `￥${Number(val).toFixed(2)}` : '-'}</Text>
      ),
    },
    {
      title: '交易手续费支出 (元)',
      dataIndex: 'transactionFeeExpense',
      key: 'transactionFeeExpense',
      width: 180,
      render: (val) => (val !== undefined ? `￥${Number(val).toFixed(2)}` : '-'),
    },
    {
      title: '交易手续费退回 (元)',
      dataIndex: 'transactionFeeReturn',
      key: 'transactionFeeReturn',
      width: 170,
      render: (val) => (val !== undefined ? `￥${Number(val).toFixed(2)}` : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_, record) => (
        <Space size="middle">
          <a
            onClick={() =>
              history.push({
                pathname: '/reconciliationManagement/transactionDetails',
                state: {
                  startTime: dayjs(record.billDate).format('YYYY-MM-DD 00:00:00'),
                  endTime: dayjs(record.billDate).format('YYYY-MM-DD 23:59:59'),
                },
              })
            }
          >
            详情
          </a>
        </Space>
      ),
    },
  ];

  const menuProps = {
    items: [
      { key: 'all', label: '导出全部', onClick: () => handleExport('all') },
      { key: 'selected', label: '导出选中', onClick: () => handleExport('selected') },
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
            <Col className="gutter-row" span={6}>
              <Form.Item
                label="账单时间"
                name="time"
                initialValue={[dayjs().startOf('month'), dayjs()]}
              >
                <RangePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
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
                <Button className="mL15" onClick={handleReset}>重置</Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>

      <div style={{ backgroundColor: '#fff', padding: 20 }}>
        <Row gutter={16}>
          <Col span={6}>
            <h1 style={{ fontWeight: '600', fontSize: '18px' }}>交易汇总</h1>
          </Col>
          <Col
            span={18}
            style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}
          >
            <Dropdown menu={menuProps} placement="bottomRight">
              <Button type="primary">导出</Button>
            </Dropdown>
          </Col>
        </Row>

        <Table
          style={{ marginTop: 15 }}
          rowSelection={{
            selectedRowKeys,
            onChange: setSelectedRowKeys,
          }}
          rowKey="billDate"
          columns={columns}
          dataSource={list}
          pagination={{
            showSizeChanger: false,
            pageSize: searchParams.pageSize,
            total,
            current: searchParams.pageNum,
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

export default TransactionSummary;