import { Empty, Typography } from 'antd';
import { useTranslation } from 'react-i18next';

export default function Placeholder({ titleKey, messageKey = 'placeholder' }: { titleKey: string; messageKey?: string }) {
  const { t } = useTranslation();
  return (
    <>
      <Typography.Title level={2}>{t(titleKey)}</Typography.Title>
      <Empty description={t(messageKey)} />
    </>
  );
}
