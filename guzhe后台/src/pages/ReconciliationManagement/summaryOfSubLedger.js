import {
  exportSubLedgerSummary,
  getSubLedgerSummaryList,
} from '@/services/transaction';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  message,
  Row,
  Table,
  Typography,
} from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { history } from 'umi';

const { RangePicker } = DatePicker;
const { Text } = Typography;

const SummaryOfSubLedger = () => {
  const [form] = Form.useForm();
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
      const res = await getSubLedgerSummaryList(params);
      if (res.code === 10000 || res.code === 200) {
        let list = res.data?.list || [];
        list = list.sort(
          (a, b) => dayjs(b.billDate).valueOf() - dayjs(a.billDate).valueOf(),
        );
        setListData(list);
        setTotalCount(res.data?.total || 0);
      }
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData(searchParams);
  }, []);

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
    const defaultParams = {
      startTime: dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
      endTime: dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
      pageNum: 1,
      pageSize: 10,
    };
    setSearchParams(defaultParams);
    loadData(defaultParams);
  };

  const handleExport = () => {
    exportSubLedgerSummary(searchParams).then((res) => {
      if (!(res instanceof Blob)) {
        message.error(res?.msg || '导出失败，无权限访问');
        return;
      }
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      const objectUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = objectUrl;
      a.download = `分账汇总_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`;
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
    },
    {
      title: '分账金额 (元)',
      dataIndex: 'divideAmount',
      key: 'divideAmount',
      align: 'left',
      render: (val) =>
        val !== undefined ? `￥${Number(val).toFixed(2)}` : '￥0.00',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <a
          onClick={() =>
            history.push({
              pathname: '/reconciliationManagement/billingDetails',
              state: {
                startTime: dayjs(record.billDate).format('YYYY-MM-DD 00:00:00'),
                endTime: dayjs(record.billDate).format('YYYY-MM-DD 23:59:59'),
              },
            })
          }
        >
          详情
        </a>
      ),
    },
  ];

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
                label="账单日期"
                name="time"
                initialValue={[dayjs().startOf('month'), dayjs()]}
              >
                <RangePicker format="YYYY-MM-DD" style={{ width: '100%' }} />
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
                <Button onClick={handleReset} style={{ marginLeft: 8 }}>
                  重置
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>

      <div style={{ backgroundColor: '#fff', padding: 20 }}>
        <Row gutter={16} align="middle">
          <Col span={12}>
            <h1 style={{ fontWeight: '600', fontSize: '18px', margin: 0 }}>
              分账汇总
            </h1>
          </Col>
          <Col
            span={12}
            style={{ display: 'flex', justifyContent: 'flex-end' }}
          >
            <Button type="primary" onClick={handleExport}>
              导出
            </Button>
          </Col>
        </Row>

        <Table
          style={{ marginTop: 15 }}
          rowKey="billDate"
          columns={columns}
          dataSource={listData}
          pagination={{
            showSizeChanger: false,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`,
            pageSize: searchParams.pageSize,
            total: totalCount,
            current: searchParams.pageNum,
            onChange: (page) => {
              const newParams = { ...searchParams, pageNum: page };
              setSearchParams(newParams);
              loadData(newParams);
            },
          }}
          loading={loading}
        />
      </div>
    </PageContainer>
  );
};

export default SummaryOfSubLedger;
