package Pertemuan12;

import Pertemuan12.DataRoti;
import Pertemuan12.Pelanggan;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-11-22T17:47:08", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Transaksi.class)
public class Transaksi_ { 

    public static volatile SingularAttribute<Transaksi, String> idTransaksi;
    public static volatile SingularAttribute<Transaksi, Integer> totalHarga;
    public static volatile SingularAttribute<Transaksi, DataRoti> idRoti;
    public static volatile SingularAttribute<Transaksi, Pelanggan> idPelanggan;
    public static volatile SingularAttribute<Transaksi, Date> tanggal;
    public static volatile SingularAttribute<Transaksi, Integer> jumlahBeli;

}