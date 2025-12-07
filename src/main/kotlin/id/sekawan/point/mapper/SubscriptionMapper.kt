package id.sekawan.point.mapper

import com.github.davidmoten.rx.jdbc.ResultSetMapper
import id.sekawan.point.util.mymodel.Subscription
import java.sql.ResultSet

class SubscriptionMapper : ResultSetMapper<Subscription> {
    override fun call(rs: ResultSet): Subscription {
        val model = Subscription()

        model.id = rs.getString("id")
        model.name = rs.getString("name")
        model.price = rs.getLong("price")
        model.priceDescription = rs.getString("price_description")
        model.durationDays = rs.getInt("duration_days")
        model.hasMaxCycleCount = rs.getBoolean("has_max_cycle_count")
        model.maxCycleCount = rs.getInt("max_cycle_count")
        model.holderHeadOfficeId = rs.getInt("holder_head_office_id")
        model.penaltyFee = rs.getLong("penalty_fee")
        model.serviceFee = rs.getLong("service_fee")

        return model
    }
}