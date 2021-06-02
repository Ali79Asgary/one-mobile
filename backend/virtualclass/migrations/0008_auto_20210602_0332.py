# Generated by Django 3.1.3 on 2021-06-02 03:32

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('virtualclass', '0007_transaction'),
    ]

    operations = [
        migrations.AlterField(
            model_name='transaction',
            name='amount',
            field=models.DecimalField(blank=True, decimal_places=2, max_digits=19, null=True, verbose_name='amount'),
        ),
        migrations.AlterField(
            model_name='transaction',
            name='bank_track_id',
            field=models.TextField(blank=True, null=True, unique=True),
        ),
        migrations.AlterField(
            model_name='transaction',
            name='hashed_card_number',
            field=models.TextField(blank=True, null=True, verbose_name='hashed card number'),
        ),
        migrations.AlterField(
            model_name='transaction',
            name='idpay_track_id',
            field=models.IntegerField(blank=True, null=True, unique=True),
        ),
        migrations.AlterField(
            model_name='transaction',
            name='payment_id',
            field=models.TextField(blank=True, null=True, unique=True, verbose_name='payment id'),
        ),
    ]