import { useEffect, useState } from 'react';
import { Alert, Table, Tag, Typography } from 'antd';
import { useTranslation } from 'react-i18next';
import { api, type WorkOrder } from '../api';

export default function WorkOrders() {
  const { t, i18n } = useTranslation();
  const [rows, setRows] = useState<WorkOrder[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api.workOrders()
      .then(setRows)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, []);

  return (
    <>
      <Typography.Title level={2}>{t('workOrders.title')}</Typography.Title>
      {error && <Alert type="error" message={t('workOrders.loadError')} style={{ marginBottom: 16 }} />}
      <Table<WorkOrder>
        rowKey="id"
        loading={loading}
        dataSource={rows}
        columns={[
          { title: t('workOrders.reference'), dataIndex: 'reference' },
          {
            title: t('workOrders.step'),
            dataIndex: 'currentStep',
            render: (step: number) => <Tag color="blue">{t('workOrders.stepOf', { step })}</Tag>,
          },
          {
            title: t('workOrders.created'),
            dataIndex: 'createdAt',
            // Locale-aware dates follow the UI language (fixes the prototype's hard-coded en-CA).
            render: (v: string) => new Date(v).toLocaleDateString(i18n.language === 'fr' ? 'fr-CA' : 'en-CA'),
          },
        ]}
      />
    </>
  );
}
