package com.group2.sp25swp391group2se1889vj.warehouse.mapper;

import com.group2.sp25swp391group2se1889vj.user.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.warehouse.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.warehouse.entity.Warehouse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
/**
 * WarehouseMapper
 * - Đây là class dùng để chuyển đổi dữ liệu từ entity sang DTO và ngược lại
 * - Chuyển đổi dữ liệu từ WarehouseDTO sang Warehouse
 * - Chuyển đổi dữ liệu từ Warehouse sang WarehouseDTO
 */
public class WarehouseMapper {
    // Khai báo một tham chiếu đến một đối tượng UserRepository để thực hiện việc tìm kiếm User và thiết lập cho Inventory hoặc InventoryDTO
    private final UserRepository userRepository;
    // Khai báo một tham chiếu đến một đối tượng ModelMapper để thực hiện việc chuyển đổi dữ liệu giữa Warehouse và WarehouseDTO
    ModelMapper modelMapper = new ModelMapper();

    // Constructor
    public WarehouseMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * mapToWarehouseDTO
     * - Phương thức này dùng để chuyển đổi dữ liệu từ Warehouse sang WarehouseDTO
     * @param warehouse là đối tượng Warehouse
     *                  - Đối tượng này chứa thông tin về kho hàng
     *                  - Được sử dụng để chuyển đổi dữ liệu sang WarehouseDTO
     * @return WarehouseDTO
     */

    public WarehouseDTO mapToWarehouseDTO(Warehouse warehouse) {
        // Sử dụng modelMapper để chuyển đổi dữ liệu từ Warehouse sang WarehouseDTO
        WarehouseDTO warehouseDTO = modelMapper.map(warehouse, WarehouseDTO.class);
        // Thiết lập chủ sở hữu cho WarehouseDTO
        warehouseDTO.setOwnerId(warehouse.getOwner().getId());
        return warehouseDTO;
    }

    /**
     * mapToWarehouseDTO
     * - Phương thức này dùng để chuyển đổi dữ liệu từ WarehouseDTO sang Warehouse
     * @param warehouseDTO là đối tượng WarehouseDTO
     *                      - Đối tượng này chứa thông tin về kho hàng
     *                      - Được sử dụng để chuyển đổi dữ liệu sang Warehouse
     * @return Warehouse
     */
    public Warehouse mapToWarehouse(WarehouseDTO warehouseDTO) {
        // Sử dụng modelMapper để chuyển đổi dữ liệu từ WarehouseDTO sang Warehouse
        Warehouse warehouse = modelMapper.map(warehouseDTO, Warehouse.class);
        // Thiết lập chủ sở hữu cho Warehouse
        warehouse.setOwner(userRepository.findById(warehouseDTO.getOwnerId()).orElse(null));
        return warehouse;
    }


}
